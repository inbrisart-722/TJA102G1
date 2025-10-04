// sweetalert-common.js
document.addEventListener("DOMContentLoaded", function () {
    // -----------------------------
    // 公告用 - 刪除確認提示
    // -----------------------------
    var deleteForms = document.querySelectorAll("form[action$='/platform/delete']");
    for (var i = 0; i < deleteForms.length; i++) {
        deleteForms[i].addEventListener("submit", function (e) {
            e.preventDefault();
            var form = this;
            Swal.fire({
                title: '確定要刪除這則公告嗎？',
                text: '刪除後將無法復原！',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: '刪除',
                cancelButtonText: '取消'
            }).then(function (result) {
                if (result.isConfirmed) {
                    form.submit();
                }
            });
        });
    }

		// -----------------------------
	    // 公告用 - 新增 / 修改成功提示
	    // -----------------------------
	    var urlParams = new URLSearchParams(window.location.search);
	    var success = urlParams.get('success');

	    if (success === 'add') {
	        Swal.fire({
	            title: '新增成功！',
	            text: '公告已成功新增。',
	            icon: 'success',
	            showConfirmButton: false,
	            timer: 1500,
	            timerProgressBar: true
	        });

	        setTimeout(function () {
	            window.location.href = '/platform/index';
	        }, 1600);
	    }

	    if (success === 'edit') {
	        Swal.fire({
	            title: '修改成功！',
	            text: '公告內容已更新。',
	            icon: 'success',
	            showConfirmButton: false,
	            timer: 1500,
	            timerProgressBar: true
	        });

	        setTimeout(function () {
	            window.location.href = '/platform/index';
	        }, 1600);
	    }
	});
