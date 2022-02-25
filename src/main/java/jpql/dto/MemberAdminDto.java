package jpql.dto;

public class MemberAdminDto {
    private String username;
    private int age;
    private String adminName;

    public MemberAdminDto() {
    }

    public MemberAdminDto(String username, int age, String adminName) {
        this.username = username;
        this.age = age;
        this.adminName = adminName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
