$(".message a").click(function () {
    $(".register-form").toggle("slow");
  });
  $(".message a").click(function () {
    $(".login-form").toggle("slow");
  });

function myFunction() {
    var x = document.getElementById("password");
    if (x.type === "password") {
      x.type = "text";
    } else {
      x.type = "password";
    }
  }

  function Function() {
      var x = document.getElementById("confirmPassword" , "newPassword");
      if (x.type === "password") {
        x.type = "text";
      } else {
        x.type = "password";
      }
    }