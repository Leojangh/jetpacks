#![allow(non_snake_case)]

use std::borrow::Borrow;
use jni::JNIEnv;
use jni::objects::{JByteArray, JClass, JString};
use jni::sys::{jboolean, jsize, jstring};


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
pub extern "system" fn Java_com_genlz_jetpacks_libnative_RustNatives_search<'local>(mut env: JNIEnv<'local>,
// This is the class that owns our static method. It's not going to be used,
// but still must be present to match the expected signature of a static
// native method.
                                                                                    _class: JClass<'local>,
                                                                                    bytes: JByteArray<'local>,
                                                                                    pattern: JString<'local>)
                                                                                    -> jboolean {
    let pattern: String = env
        .get_string(&pattern)
        .expect("Couldn't get java string!")
        .into();
    let len = env.get_array_length(&bytes).unwrap();
    let mut jbytes = vec![0; len as usize];
    env.get_byte_array_region(bytes, 0 as jsize, &mut jbytes).unwrap();
    let b: Vec<u8> = jbytes.iter().map(|b| *b as u8).collect();
    matches(&b, pattern.as_str()) as jboolean
}


struct BruteForceImpl {
    pattern: Vec<u16>,
}

impl BruteForceImpl {
    fn new(pattern: &str) -> Self {
        let pattern = compile_pattern_string(pattern);
        Self { pattern }
    }

    fn bf(arr: &[u8], sub: &[u16]) -> Option<usize> {
        for (i, window) in arr.windows(sub.len()).enumerate() {
            let mut matched = 0;
            for (i, &b) in window.iter().enumerate() {
                if b == sub[i] as u8 || sub[i] == WILDCARD {
                    matched += 1;
                    continue;
                } else { break; }
            }
            if matched == sub.len() {
                return Some(i);
            }
        };
        None
    }
}

impl BytePattern for BruteForceImpl {
    fn index_in(&self, bytes: &[u8]) -> Option<usize> {
        Self::bf(bytes, &self.pattern)
    }
}


struct BmImpl {
    pattern: Vec<u16>,
}

impl BmImpl {
    fn new(pattern: &str) -> Self {
        let pattern = compile_pattern_string(pattern);
        Self { pattern }
    }

    fn boyer_moore_search(haystack: &[u8], pattern: &[u16]) -> Option<usize> {
        let haystack_len = haystack.len();
        let needle_len = pattern.len();

        if needle_len == 0 {
            return Some(0);
        }

        if needle_len > haystack_len {
            return None;
        }

        let mut bad_char_table = if pattern.contains(&WILDCARD) {
            let first_wildcard_position = pattern.iter().position(|&x| x == WILDCARD).unwrap();
            [first_wildcard_position; 0x100]
        } else {
            [needle_len; 0x100]
        };

        for (i, &c) in pattern.iter().enumerate().take(needle_len - 1) {
            if c != WILDCARD {
                bad_char_table[c as usize] = needle_len - i - 1;
            }
        }

        let mut i = needle_len - 1;

        while i < haystack_len {
            let mut j = needle_len - 1;

            while haystack[i] == pattern[j] as u8 || pattern[j] == WILDCARD {
                if j == 0 {
                    return Some(i - j);
                }

                i -= 1;
                j -= 1;
            }

            i += std::cmp::max(bad_char_table[haystack[i] as usize], needle_len - j);
        }

        None
    }
}

impl BytePattern for BmImpl {
    fn index_in(&self, bytes: &[u8]) -> Option<usize> {
        Self::boyer_moore_search(bytes, self.pattern.borrow())
    }
}

const WILDCARD: u16 = 0xffff;

pub trait BytePattern {
    fn index_in(&self, bytes: &[u8]) -> Option<usize>;
}

/// Convenient function for only used once.
pub fn matches(bytes: &[u8], pattern: &str) -> bool {
    compile(pattern, "bm").index_in(bytes) != None
}

pub fn compile(pattern: &str, algorithm: &str) -> Box<dyn BytePattern> {
    match algorithm.to_lowercase().as_str() {
        "bm" => { Box::new(BmImpl::new(pattern)) }
        "bf" => { Box::new(BruteForceImpl::new(pattern)) }
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
            if it == "??" { WILDCARD } else {
                u16::from_str_radix(it, 16).expect(format!("Illegal argument: {}", pattern).as_str())
            }
        ).collect()
}

#[cfg(test)]
mod tests {
    use super::*;


    #[test]
    fn it_works() {
        let pattern = compile("ca fe ba be", "bm");

        let bytes = [0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);
        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, false);

        let bytes = [0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);
        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, false);

        let bytes = [0xca, 0xca, 0xfe, 0xbe, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, false);

        let pattern = compile("ca ?? ba ??", "bm");

        let bytes = [0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes), Some(1));
        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xbe];
        assert_eq!(pattern.index_in(&bytes) != None, true);
        let bytes = [0xca, 0xca, 0xfe, 0xba, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, true);

        let bytes = [0xca, 0xca, 0xfe, 0xbe, 0xba];
        assert_eq!(pattern.index_in(&bytes) != None, false);

        let tail = [
            0x06u8, 0x20, 0x00, 0x00, 0x91, 0x15, 0x00, 0x00, 0x34, 0xe1, 0x96, 0x00, 0x00, 0x10,
            0x00, 0x00,
        ];

        let pattern = compile("06 20 00 00 91 15 00 00 34 e1 96 00 00 10 00 00", "bm");
        assert_eq!(pattern.index_in(&tail) != None, true);

        let pattern = compile("20 00 00 91 15 00 00 34 e1 96 00 00 10 00 00", "bm");
        assert_eq!(pattern.index_in(&tail).unwrap(), 1);

        let pattern = compile("fe 00 00 91 15 00 00 34 e1 96 00 00 10 00 00", "bm");
        assert_eq!(pattern.index_in(&tail), None);

        let pattern = compile("?? 00 00 91 15 00 00 34 e1 96 00 00 10 00 00", "bm");
        assert_eq!(pattern.index_in(&tail), Some(1));

        let pattern = compile("?? 00 fe 91 15 00 00 34 e1 96 00 00 10 00 00", "bm");
        assert_eq!(pattern.index_in(&tail), None);

        let pattern = compile("?? 00 00 91 15 00 00 34 e1 96 00 00 10 00 01", "bm");
        assert_eq!(pattern.index_in(&tail), None);

        let pattern = compile("?? 00 00 91 15 00 00 34 e1 96 ?? 00 10 00 01", "bm");
        assert_eq!(pattern.index_in(&tail), None);

        let pattern = compile("01", "bm");
        assert_eq!(pattern.index_in(&[0x00]), None);
    }

    #[test]
    fn test_real_life() {
        let tail = "06 20 ?? 00 91 15 00 00 34 e1 96 00 00 10 00 ??";
        let f = std::fs::read("../../../../sampledata/classes.dex").unwrap();
        //在我的M1pro机器上，debug模式下，直接运行这个test，不精确的运行结果表明，使用暴力匹配算法运行这个test的时长在400ms左右，而使用优化的bm算法耗时约为120ms
        let pattern = compile(tail, "bm");
        if let Some(index) = pattern.index_in(&f) {
            print!("{:x}", index);
        } else {
            print!("None");
        }
    }
}
