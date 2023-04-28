#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::io::{BufReader};
use std::path::Path;
use std::sync::{Arc, Mutex};
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

#[derive(Debug)]
struct KmpImpl {
    pattern: Vec<u16>,
}

pub trait BytePattern {
    fn matches(&self, bytes: &[u8]) -> bool;
}

impl KmpImpl {
    fn new(pattern: &str) -> Self {
        let pattern = regex::Regex::new(r"\s+")
            .unwrap()
            .split(pattern.trim())
            .map(|it|
                u16::from_str_radix(it, 16).expect(format!("Illegal argument: {}", pattern).as_str())
            ).collect();
        KmpImpl { pattern }
    }

    fn match_internal(bytes: &[u8], pattern: &[u16]) -> bool {
        const CONCURRENCY_THRESHOLD: usize = 400_000;
        if bytes.len() < CONCURRENCY_THRESHOLD {
            return Self::kmp(&bytes[..], bytes.len(), pattern) != -1;
        }

        const CONCURRENCY: u8 = 4;
        let len = bytes.len();
        let (tx, rx) = std::sync::mpsc::channel();
        // let (signal_tx, signal_rx) = std::sync::mpsc::channel();
        std::thread::scope(move |s| {
            for t in 0..CONCURRENCY {
                let from = (len as f32 * (t as f32) / CONCURRENCY as f32) as usize;
                let tx = tx.clone();
                let h = s.spawn(move || {
                    let contains = Self::kmp(&bytes[from..], len, &pattern) != -1;
                    println!("thread: {} start at: {}, result: {}", t, from, contains);
                    tx.send(contains).unwrap();
                });
            }
        });

        for contains in rx {
            if contains { return true; }
        }
        return false;
    }

    fn kmp(bytes: &[u8], to: usize, pattern: &[u16]) -> i64 {
        if bytes.len() < pattern.len() { return -1; }
        let n = to;
        let m = pattern.len();
        let lps = Self::compute_lps(&pattern);
        let mut j = 0;
        let mut i = 0;
        while i < n {
            if pattern[j] as u8 == bytes[i] {
                i += 1;
                j += 1;
            }
            if j == m {
                return (i - j) as i64;
            } else if i < n && pattern[j] as u8 != bytes[i] {
                if j != 0 {
                    j = lps[j - 1];
                } else {
                    i += 1;
                }
            }
        }
        return -1;
    }

    fn compute_lps(pattern: &[u16]) -> Vec<usize> {
        let m = pattern.len();
        let mut lps = vec![0; m];
        let mut len = 0usize;
        let mut i = 1usize;
        while i < m {
            if pattern[i] == pattern[len] {
                len += 1;
                lps[i] = len;
                i += 1;
            } else {
                if len != 0 {
                    len = lps[len - 1]
                } else {
                    lps[i] = 0;
                    i += 1;
                }
            }
        }
        return lps;
    }
}

impl BytePattern for KmpImpl {
    fn matches(&self, bytes: &[u8]) -> bool {
        Self::match_internal(bytes, &self.pattern)
    }
}

#[cfg(test)]
mod tests {
    use rand::distributions::Uniform;
    use rand::Rng;
    use super::*;

    #[test]
    fn it_works() {
        let pattern = KmpImpl::new("ca fe ba be");
        let mut rng = rand::thread_rng();
        // let rds: Vec<u8> = rng.sample_iter(Uniform::from(0..255)).take(10_0_000).collect();

        let mut bytes = vec![0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.matches(&bytes), true);

        let bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.matches(&bytes), true);
        //
        let bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.matches(&bytes), false);

        let bytes = vec![0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.matches(&bytes), true);

        let bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.matches(&bytes), true);
        let bytes = vec![0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.matches(&bytes), false);

        let bytes = vec![0xca, 0xca, 0xfe, 0xbe, 0xba];
        assert_eq!(pattern.matches(&bytes), false);
    }

    #[test]
    fn test_real_life() {
        let pattern = KmpImpl::new("06 20 00 00 91 15 00 00 34 e1 96 00 00 10 00 00");
        let f = std::fs::read("../../../../sampledata/classes.dex").unwrap();
        println!("{}", f.len());
        println!("{}", pattern.matches(&f));
    }
}
