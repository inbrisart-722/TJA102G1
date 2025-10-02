package com.eventra.member.github;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubGetUserReqDTO {
    public String login; // 使用者帳號（例如 octocat）
    public Integer id; // 使用者的唯一數字 ID
    @JsonProperty("node_id")
    public String nodeId; // GraphQL Node ID
    @JsonProperty("avatar_url")
    public String avatarUrl; // 大頭貼 URL
    @JsonProperty("html_url")
    public String htmlUrl; // 使用者 GitHub 頁面連結
    public String type; // User / Organization
    @JsonProperty("site_admin")
    public Boolean siteAdmin; // 是否是 GitHub site admin
    public String name; // 使用者設定的顯示名稱
    public String company; // 公司
    public String blog; // 個人網站或部落格 URL
    public String location; // 地區
    public String email; // ⚠️ email 常是 null，要另外打 /user/emails 才能確保拿到
    public Boolean hireable; // 是否開放被招聘
    public String bio; // 自我介紹
    @JsonProperty("twitter_username")
    public String twitterUsername; // 推特帳號
    @JsonProperty("public_repos")
    public Integer publicRepos; // 公開 repo 數
    @JsonProperty("public_gists")
    public Integer publicGists; // 公開 gist 數
    public Integer followers; // 粉絲數
    public Integer following; // 追蹤數
    @JsonProperty("created_at")
    public String createdAt; // 帳號建立時間
    @JsonProperty("updated_at")
    public String updatedAt; // 最後更新時間
    @JsonProperty("private_gists")
    public Integer privateGists; // 私有 gist 數（需要 scope）
    @JsonProperty("total_private_repos")
    public Integer totalPrivateRepos; // 私有 repo 總數（需要 scope）
    @JsonProperty("owned_private_repos")
    public Integer ownedPrivateRepos; // 自己擁有的私有 repo 數
    @JsonProperty("two_factor_authentication")
    public Boolean twoFactorAuthentication; // 是否有開啟 2FA

    /** 方案資訊（storage, collaborators, private_repos 數量） */
    @JsonProperty("plan")
    public Plan plan;

    /**
     * Sub-object: plan info
     */
    public static class Plan {
        /** 方案名稱 (例如 free, pro) */
        @JsonProperty("name")
        public String name;

        /** 可用空間 */
        @JsonProperty("space")
        public Integer space;

        /** 協作者數量 */
        @JsonProperty("collaborators")
        public Integer collaborators;

        /** 可用 private repos 數量 */
        @JsonProperty("private_repos")
        public Integer privateRepos;
    }
    
    // getter, setter
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getHtmlUrl() {
		return htmlUrl;
	}
	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getSiteAdmin() {
		return siteAdmin;
	}
	public void setSiteAdmin(Boolean siteAdmin) {
		this.siteAdmin = siteAdmin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBlog() {
		return blog;
	}
	public void setBlog(String blog) {
		this.blog = blog;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getHireable() {
		return hireable;
	}
	public void setHireable(Boolean hireable) {
		this.hireable = hireable;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getTwitterUsername() {
		return twitterUsername;
	}
	public void setTwitterUsername(String twitterUsername) {
		this.twitterUsername = twitterUsername;
	}
	public Integer getPublicRepos() {
		return publicRepos;
	}
	public void setPublicRepos(Integer publicRepos) {
		this.publicRepos = publicRepos;
	}
	public Integer getPublicGists() {
		return publicGists;
	}
	public void setPublicGists(Integer publicGists) {
		this.publicGists = publicGists;
	}
	public Integer getFollowers() {
		return followers;
	}
	public void setFollowers(Integer followers) {
		this.followers = followers;
	}
	public Integer getFollowing() {
		return following;
	}
	public void setFollowing(Integer following) {
		this.following = following;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Integer getPrivateGists() {
		return privateGists;
	}
	public void setPrivateGists(Integer privateGists) {
		this.privateGists = privateGists;
	}
	public Integer getTotalPrivateRepos() {
		return totalPrivateRepos;
	}
	public void setTotalPrivateRepos(Integer totalPrivateRepos) {
		this.totalPrivateRepos = totalPrivateRepos;
	}
	public Integer getOwnedPrivateRepos() {
		return ownedPrivateRepos;
	}
	public void setOwnedPrivateRepos(Integer ownedPrivateRepos) {
		this.ownedPrivateRepos = ownedPrivateRepos;
	}
	public Boolean getTwoFactorAuthentication() {
		return twoFactorAuthentication;
	}
	public void setTwoFactorAuthentication(Boolean twoFactorAuthentication) {
		this.twoFactorAuthentication = twoFactorAuthentication;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	
	
	@Override
	public String toString() {
		return "GitHubGetUserReqDTO [login=" + login + ", id=" + id + ", nodeId=" + nodeId + ", avatarUrl=" + avatarUrl
				+ ", htmlUrl=" + htmlUrl + ", type=" + type + ", siteAdmin=" + siteAdmin + ", name=" + name
				+ ", company=" + company + ", blog=" + blog + ", location=" + location + ", email=" + email
				+ ", hireable=" + hireable + ", bio=" + bio + ", twitterUsername=" + twitterUsername + ", publicRepos="
				+ publicRepos + ", publicGists=" + publicGists + ", followers=" + followers + ", following=" + following
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", privateGists=" + privateGists
				+ ", totalPrivateRepos=" + totalPrivateRepos + ", ownedPrivateRepos=" + ownedPrivateRepos
				+ ", twoFactorAuthentication=" + twoFactorAuthentication + ", plan=" + plan + "]";
	}
}
