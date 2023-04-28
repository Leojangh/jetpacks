#![allow(non_snake_case)]

extern crate core;

use std::borrow::Borrow;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;


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


struct BmImpl {
    pattern: Vec<u16>,
}

impl BmImpl {
    fn new(pattern: &str) -> Self {
        let pattern = compile_pattern_string(pattern);
        Self { pattern }
    }

    fn boyer_moore_search(bytes: &[u8], pattern: &[u16]) -> bool {
        let haystack_len = bytes.len();
        let needle_len = pattern.len();

        if needle_len == 0 {
            return true;
        }

        if needle_len > haystack_len {
            return false;
        }

        let mut bad_char_table = [needle_len; 256];

        for (i, &c) in pattern.iter().enumerate().take(needle_len - 1) {
            bad_char_table[c as usize] = needle_len - i - 1;
        }

        let mut i = needle_len - 1;

        while i < haystack_len {
            let mut j = needle_len - 1;

            while bytes[i] == pattern[j] as u8 {
                if j == 0 {
                    return true;
                }

                i -= 1;
                j -= 1;
            }

            i += std::cmp::max(bad_char_table[bytes[i] as usize], needle_len - j);
        }

        false
    }
}

impl BytePattern for BmImpl {
    fn matches(&self, bytes: &[u8]) -> bool {
        Self::boyer_moore_search(bytes, self.pattern.borrow())
    }
}

pub trait BytePattern {
    fn matches(&self, bytes: &[u8]) -> bool;
}

pub fn compile(pattern: &str, algorithm: &str) -> Box<dyn BytePattern> {
    match algorithm.to_lowercase().as_str() {
        "bm" => { Box::new(BmImpl::new(pattern)) }
        unknown => {
            panic!("{}", format!("No such algorithm:{unknown}"));
        }
    }
}

fn compile_pattern_string(pattern: &str) -> Vec<u16> {
    regex::Regex::new(r"\s+")
        .unwrap()
        .split(pattern.trim())
        .map(|it|
            u16::from_str_radix(it, 16).expect(format!("Illegal argument: {}", pattern).as_str())
        ).collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let pattern = compile("ca fe ba be", "bm");

        let bytes = vec![0xca, 0xfe, 0xba, 0xbe];
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
        let tail = [0x06u16, 0x20, 0x00, 0x00, 0x91, 0x15, 0x00, 0x00, 0x34, 0xe1, 0x96, 0x00, 0x00, 0x10, 0x00, 0x00];
        let pattern = "06 20 00 00 91 15 00 00 34 e1 96 00 00 10 00 00";
        let f = std::fs::read("../../../../sampledata/classes.dex").unwrap();
        let pattern = compile(pattern, "bm");
        let b = pattern.matches(f.as_slice());
        print!("{b}");
    }
}
