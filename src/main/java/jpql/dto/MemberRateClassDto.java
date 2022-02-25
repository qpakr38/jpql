package jpql.dto;

public class MemberRateClassDto {
    private String username;
    private int age;
    private String rateClassName;

    public MemberRateClassDto() {
    }

    public MemberRateClassDto(String username, int age, String rateClassName) {
        this.username = username;
        this.age = age;
        this.rateClassName = rateClassName;
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

    public String getRateClassName() {
        return rateClassName;
    }

    public void setRateClassName(String rateClassName) {
        this.rateClassName = rateClassName;
    }
}
