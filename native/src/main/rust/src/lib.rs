#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::io::{BufReader};
use std::path::Path;
use std::sync::{Arc};
use std::sync::atomic::{AtomicUsize, Ordering};


// This keeps Rust from "mangling" the name and making it unique for this
// crate.
#[no_mangle]
pub extern "system" fn Java_com_genlz_jetpacks_libnative_RustNatives_hello<'local>(mut env: JNIEnv<'local>,
// This is the class that owns our static method. It's not going to be used,
// but still must be present to match the expected signature of a static
// native method.
                                                                                   _class: JClass<'local>,
                                                                                   input: JString<'local>)
                                                                                   -> jstring {
    // First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works.
    let input: String =
        env.get_string(&input).expect("Couldn't get java string!").into();

    // Then we have to create a new Java string to return. Again, more info
    // in the `strings` module.
    let output = env.new_string(format!("Hello, {}!", input))
        .expect("Couldn't create java string!");

    // Finally, extract the raw pointer to return.
    output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_genlz_jetpacks_libnative_RustNatives_parse<'local>(mut env: JNIEnv<'local>,
// This is the class that owns our static method. It's not going to be used,
// but still must be present to match the expected signature of a static
// native method.
                                                                                   _class: JClass<'local>,
                                                                                   zipPath: JString<'local>) {
    let str: String = env.get_string(&zipPath).unwrap().into();
    let fname = Path::new(&str);
    let file = std::fs::File::open(fname).unwrap();
    let reader = BufReader::new(file);
    let mut archive = zip::ZipArchive::new(reader).unwrap();
    for i in 0..archive.len() {
        let file = archive.by_index(i).unwrap();
        let path = match file.enclosed_name() {
            None => {
                print!("Entry {} has a suspicious path", file.name());
                continue;
            }
            Some(path) => path
        };
        {
            let comment = file.comment();
            if !comment.is_empty() {
                print!("Entry {i} comment: {comment}");
            }
        }

        if (*file.name()).ends_with("/") {
            print!("Entry {} is a dir with name \"{}\"", i, path.display());
        } else {
            print!("Entry {} is a file with name \"{}\" ({} bytes)", i, path.display(), file.size());
        }
    }
}

#[derive(Debug)]
struct KmpImpl {
    pattern: Vec<u16>,
}

pub trait BytePattern {
    fn matches(&self, bytes: &Vec<u8>) -> bool;
}

impl KmpImpl {
    fn new(pattern: &str) -> Self {
        let black = regex::Regex::new(r"\s+").unwrap();
        let bytePattern: Vec<&str> = black.split(pattern.trim()).collect();
        let mut pattern = vec![0u16; bytePattern.len()];
        for (index, value) in bytePattern.iter().enumerate() {
            pattern[index] = if *value == "??" { 0xffff } else {
                u16::from_str_radix(*value, 16).unwrap()
            }
        }
        KmpImpl {
            pattern
        }
    }

    fn concurrent_match(bytes: &Vec<u8>, pattern: &Vec<u16>) -> bool {
        const CONCURRENCY: u8 = 4u8;
        let bytes = bytes.clone();
        let pattern = pattern.clone();
        let len = bytes.len();
        let fence = Arc::new(AtomicUsize::new(len));
        let bytes = Arc::new(bytes);
        let pattern = Arc::new(pattern);
        let (tx, rx) = std::sync::mpsc::channel();
        std::thread::scope(|s| {
            for t in 0..CONCURRENCY {
                let from = (len as f32 * (t as f32) / CONCURRENCY as f32) as usize;
                let pattern = pattern.clone();
                let to = fence.clone();
                let bytes = bytes.clone();
                let tx = tx.clone();
                s.spawn(move || {
                    let contains = Self::kmp(&bytes, from, &to, &pattern) != -1;
                    if !contains {
                        to.store(from + pattern.len() - 1, Ordering::Relaxed)
                    }
                    tx.send(contains).unwrap();
                });
            }
        });

        for contains in rx {
            print!("{}", contains);
            if contains {
                return true;
            }
        }
        return false;
    }

    fn kmp(bytes: &Vec<u8>, from: usize, to: &AtomicUsize, pattern: &Vec<u16>) -> i64 {
        let m = pattern.len();
        let mut lps = vec![0; 20];
        let mut j = 0usize;
        Self::compute_lps(&pattern, &mut lps);
        let mut i = from;
        while i < (to.load(Ordering::SeqCst) - from) {
            if (pattern[j] == 0xffff) || (pattern[j] as u8 == bytes[i]) {
                i = i + 1;
                j = j + 1;
            }
            if j == m {
                return (i - j) as i64;
            } else if i < (to.load(Ordering::SeqCst) - from) && (pattern[j] != 0xffff && pattern[j] as u8 != bytes[i]) {
                if j != 0 {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return -1;
    }

    fn compute_lps(pattern: &Vec<u16>, lps: &mut Vec<usize>) {
        let m = pattern.len();
        let mut len = 0usize;
        lps[0] = 0;
        let mut i = 1usize;
        while i < m {
            if pattern[i] == pattern[len] {
                len = len + 1;
                lps[i] = len;
                i = i + 1;
            } else {
                if len != 0 {
                    len = lps[len - 1]
                } else {
                    lps[i] = 0;
                    i = i + 1;
                }
            }
        }
    }
}

impl BytePattern for KmpImpl {
    fn matches(&self, bytes: &Vec<u8>) -> bool {
        // Self::concurrent_match(bytes, &self.pattern)
        let len = bytes.len();
        return Self::kmp(bytes, 0, &AtomicUsize::new(len), &self.pattern) != -1;
    }
}


#[cfg(test)]
mod tests {
    use std::fs::File;
    use std::io::{Read, Seek};
    use std::rc::Rc;
    use std::sync::mpsc;
    use std::thread;
    use std::time::{Duration, Instant, SystemTime};
    use super::*;

    #[test]
    fn it_works() {
        let pattern = KmpImpl::new("ca fe ba be");
        let mut bytes = vec![0xca, 0xfe, 0xba, 0xbe];
        unsafe {
            let mut dex = File::open("../../../../sampledata/classes.dex").unwrap();
            println!("{}", dex.metadata().unwrap().len());
            let m = std::fs::read("../../../../sampledata/classes.dex").unwrap();
            // let m = memmap::MmapOptions::new().map(&dex).unwrap().as_ref().to_vec();
            let now = Instant::now();
            pattern.matches(&m);
            print!("{:?}", Instant::now() - now);
        }

        // assert_eq!(pattern.matches(&bytes), true);
        // bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xbe];
        // assert_eq!(pattern.matches(&bytes), true);
        // bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xba];
        // assert_eq!(pattern.matches(&bytes), false);
        //
        // let p = KmpImpl::new("ca ?? ba ??");
        // bytes = vec![0xca, 0xfe, 0xba, 0xbe];
        // assert_eq!(pattern.matches(&bytes), true);
        // bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xbe];
        // assert_eq!(pattern.matches(&bytes), true);
        // bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xba];
        // assert_eq!(pattern.matches(&bytes), false);
        //
        // bytes = vec![0xca, 0xca, 0xfe, 0xbe, 0xba];
        // assert_eq!(pattern.matches(&bytes), false);
    }
}
