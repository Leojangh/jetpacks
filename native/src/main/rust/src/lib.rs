#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::sys::jint;
use jni::objects::JClass;

#[no_mangle]
pub extern "system" fn Java_com_genlz_android_Natives_method(
    _env: JNIEnv,
    _this: JClass,
) -> jint {
    3
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        print!("Hello")
    }
}
