// g1_6_favorite_exhibitions.js
// 專門處理 展覽頁 - 收藏按鈕的初始化與切換收藏
document.addEventListener("DOMContentLoaded", () => {
	// 從 URL 取 exhibitionId
	const params = new URLSearchParams(window.location.search);
	const exhId = params.get("exhibitionId");

	if (!exhId) {
		console.error("URL 少 exhibitionId");
		return;
	}

	// 寫入 body 的 data-exh-id
	document.body.setAttribute("data-exh-id", exhId);

	// 找收藏按鈕
	const btn = document.getElementById("favorite");
	if (!btn) return;

	// 確保按鈕有展覽ID
	btn.dataset.exhId = exhId;

	// 頁面初始化, 呼叫 API 檢查收藏狀態
	csrfFetch(`/api/favorite/check?exhId=${exhId}`)
		.then(res => res.json())
		.then(data => {
			if (data.favoriteStatus) {
				btn.classList.add("active");
				btn.querySelector("i").classList.add("icon-heart-filled");
				console.log("載入頁面的初始收藏狀態 = 1");
			} else {
				btn.classList.remove("active");
				btn.querySelector("i").classList.remove("icon-heart-filled");
				console.log("載入頁面的初始收藏狀態 = 0");
			}
		})
		.catch(err =>
			console.error("初始化收藏狀態失敗: ", err)
		);

	// 綁定收藏切換 (取消後移除)
	btn.addEventListener("click", (e) => {
		e.preventDefault();

		csrfFetch("/api/front-end/protected/favorite/toggle", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: `exhId=${exhId}`
		})
			.then(res => {
				if (res.status === 401) {
					// 存path + query
					const redirectPath = window.location.pathname + window.location.search;
					sessionStorage.setItem("redirect", redirectPath);
					
					console.log("結果: ", sessionStorage.getItem("redirect"));
					
					Swal.fire({
						title: "請先登入會員",
						text: "登入後才能收藏展覽",
						icon: "warning",
						showCancelButton: true,
						confirmButtonText: "前往",
						cancelButtonText: "返回"

					}).then(result => {
						if (result.isConfirmed) {
							location.href = "/front-end/login?redirect=" + encodeURIComponent(redirectPath);
						}
					});
					return Promise.reject("未登入");
				}
				return res.json();
			})
			.then(data => {
				if (data.favoriteStatus) {
					btn.classList.add("active");
					btn.querySelector("i").classList.add("icon-heart-filled");
					Swal.fire({
						title: "已收藏展覽！", 
						icon: "success", 
						timer: 1500, 
						showConfirmButton: false 
					});
				} else {
					btn.classList.remove("active");
					btn.querySelector("i").classList.remove("icon-heart-filled");
					Swal.fire({
						title: "已取消收藏", 
						icon: "success", 
						timer: 1500, 
						showConfirmButton: false 
					});
				}
			})
			.catch(err => {
				if (err !== "未登入") console.error("切換收藏失敗:", err);
			});
	});
});
