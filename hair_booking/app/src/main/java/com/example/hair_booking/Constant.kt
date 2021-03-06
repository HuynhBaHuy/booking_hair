package com.example.hair_booking

object Constant {

     object collection {
        const val hairSalons: String = "hairSalons"
        const val accounts:String = "accounts"
        const val appointments:String = "appointments"
        const val discounts:String = "discounts"
        const val normalUsers:String = "normalUsers"
        const val services:String = "services"
        const val stylists:String = "stylists"
        const val shifts: String = "shifts"
    }

    object AppointmentStatus {
        val isPending: String = "Chưa thanh toán"
        val isCheckout: String = "Đã thanh toán"
        val isAbort: String = "Đã hủy"
    }
    const val notFoundImg = "notfoundimg"
    object messages{
        const val required:String = "Bắt buộc"
        const val invalidEmail:String = "Email không hợp lệ"
        const val loginFailedByEmail:String = "Email wrong"
        const val loginFailedByPassword:String = "Password wrong"
        const val loginFailed:String = "Email hoặc mật khẩu không đúng!"
        const val loginCredentialInvalid: String = "Lỗi xác thực"
        const val loginCredentialsExpired: String = "Phiên đăng nhập đã hết hạn"
        const val loginCredentialsCollision:String = "Tài khoản này đã được xác thực trước đó."
        const val signUpFailedByWeekPassword:String = "Mật khẩu yếu. Thử lại."
        const val signUpFailedByMalformedEmail:String = "Email xấu! Không thể đăng kí."
        const val signUpFailedByExistsEmail:String = "Email tồn tại. Thử lại"
        const val signUpSuccess:String = "Đăng kí thành công!"
        const val notMatch:String = "Không trùng khớp."
        const val errorFromSever:String = "Đã có lỗi xảy ra! Thử lại sau."
        const val bannedAccount: String = "Tài khoản này đã bị khóa."
        const val length_password_condition_msg = "Must contains at least 8 characters."
        var passwordValidationMsg: ((String) -> String) = { condition ->
            "Must contains at least one $condition character."
        }
    }
    object rating{
        const val great:String = "Vô cùng hài lòng"
        const val good: String = "Tốt"
        const val normal: String = "Bình thường"
        const val bad: String = "Khá tệ"
        const val veryBad:String = "Rất tệ."
    }
    object roles{
        const val userRole:String = "user"
        const val managerRole:String = "manager"
        const val adminRole:String = "admin"
    }

    object ImagePath {
        const val stylist: String = "booking-hair/images/stylists/"
        const val salon: String = "booking-hair/images/salons/"
    }
}