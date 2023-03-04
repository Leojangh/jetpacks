#![allow(non_snake_case)]

extern crate android_logger;
#[macro_use]
extern crate log;

use std::thread::spawn;

use android_logger::Config;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use log::{LevelFilter, log, Record};


// This keeps Rust from "mangling" the name and making it unique for this
// crate.
#[no_mangle]
pub extern "system" fn Java_com_genlz_jetpacks_libnative_RustNatives_hello<'local>(mut env: JNIEnv<'local>,
// This is the class that owns our static method. It's not going to be used,
// but still must be present to match the expected signature of a static
// native method.
                                                     class: JClass<'local>,
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
pub extern "system" fn Java_com_genlz_jetpacks_libnative_RustNatives_runNative(_env: JNIEnv,
                                                                               _class: JClass, )
{
    let cores = core_affinity::get_core_ids().expect("error");
    android_logger::init_once(Config::default().with_max_level(LevelFilter::Trace));
    println!("Hello");
    spawn(move || {
        debug!("Thread :{:?}",cores)
    });
}


#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        print!("Hello")
    }
}
