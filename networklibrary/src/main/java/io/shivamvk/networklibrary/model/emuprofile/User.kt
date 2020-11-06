/* 
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */
package io.shivamvk.networklibrary.model.emuprofile

import io.shivamvk.networklibrary.models.BaseModel


data class User (

	val _id : String,
	val checkbox1 : Checkbox1,
	val checkbox2 : Checkbox2,
	val checkbox3 : Checkbox3,
	val checkbox4 : Checkbox4,
	val checkbox5 : Checkbox5,
	val available : Boolean,
	val location : List<String>,
	val languages : List<String>,
	val symptoms : List<String>,
	val active : Boolean,
	val userType : String,
	val full_name : String,
	val email : String,
	val password : String,
	val verified : Boolean,
	val bio : String,
	val college : String,
	val experience : Int,
	val expertise : String,
	val qualification : String,
	val aadhaar : String,
	val cheque : String,
	val pan : String,
	val mobile_number: String
): BaseModel