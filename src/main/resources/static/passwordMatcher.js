function checkPasswordMatcher(){
    var password= document.getElementById("inputPassword").value;
    var repeatPassword= document.getElementById("inputRepeatPassword").value;
    var message= document.getElementById("passwordMatchMessage");
    if(password === repeatPassword){
        message.innerHTML="Password match";
        message.className="text-success"
    }else{
        message.innerHTML="Password do not match";
        message.className="text-danger" 
    }

}