package com.github.malyshevhen.testutils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.dto.UserRegistrationForm;
import com.github.malyshevhen.dto.UserUpdateForm;
import com.github.malyshevhen.domain.models.Address;
import com.github.malyshevhen.domain.models.User;

public class FakeData {

    public static User getValidUser() {
        return User.builder()
                .email(getValidEmail())
                .birthDate(getValidBirthDate())
                .firstName(getRandomFirstName())
                .lastName(getRandomLastName())
                .address(getValidAddress())
                .phone(getRandomPhone())
                .build();
    }

    public static Address getValidAddress() {
        var country = "Ukraine";
        return Address.builder()
                .city(getRandomCity())
                .country(country)
                .street(getRandomStreet())
                .number(getRandomNumber())
                .build();
    }

    public static UserRegistrationForm getValidUserRegistrationForm() {
        return new UserRegistrationForm()
                .email(getValidEmail())
                .firstName(getRandomFirstName())
                .lastName(getRandomLastName())
                .address(getValidAddress())
                .phone(getRandomPhone())
                .birthDate(getValidBirthDate());
    }

    public static UserUpdateForm getValidUserUpdateForm() {
        return new UserUpdateForm()
                .email(getValidEmail())
                .firstName(getRandomFirstName())
                .lastName(getRandomLastName())
                .address(getValidAddress())
                .phone(getRandomPhone())
                .birthDate(getValidBirthDate());
    }

    public static UserInfo getValidUserInfo() {
        return new UserInfo()
                .id(1L)
                .email(getValidEmail())
                .firstName(getRandomFirstName())
                .lastName(getRandomLastName())
                .address(getValidAddress())
                .phone(getRandomPhone())
                .birthDate(getValidBirthDate());
    }

    public static String getValidEmail() {
        var username = (getRandomFirstName() + "." + getRandomLastName()).toLowerCase();
        var subdomains = List.of("gmail", "ukr", "yahoo", "outlook", "meta");
        var domains = List.of(".com", ".ua", ".net", ".org");

        var randomSubdomain = subdomains.get(new java.util.Random().nextInt(subdomains.size()));
        var randomDomain = domains.get(new java.util.Random().nextInt(domains.size()));

        return String.format("%s@%s.%s", username, randomSubdomain, randomDomain);
    }

    public static String getRandomFirstName() {
        var firstNames = List.of("John", "Jack", "James", "Jane");
        int indexFirstName = (int) (Math.random() * firstNames.size());
        return firstNames.get(indexFirstName);
    }

    public static String getRandomLastName() {
        var lastNames = List.of("Doe", "Black", "White", "Smith");
        int indexLastName = (int) (Math.random() * lastNames.size());
        return lastNames.get(indexLastName);
    }

    public static String getRandomPhone() {
        var phonePattern = Pattern.compile("\\+[0-9]{1,3}[0-9]{8,}");
        return phonePattern.matcher(String.valueOf(Math.abs((long) (Math.random() * 10_000_000L)))).replaceAll("");
    }

    public static LocalDate getValidBirthDate() {
        return LocalDate.now().minusYears((int) Math.random() % 150 + 18);
    }

    public static String getRandomCity() {
        var cities = List.of("Kyiv", "Lviv", "Odesa", "Dnipro",
                "Zaporizhzhia", "Mykolaiv", "Chernihiv", "Sumy",
                "Poltava", "Kharkiv", "Donetsk", "Mariupol", "Lugansk",
                "Simferopol", "Dnipropetrovsk");
        int indexCity = (int) (Math.random() * cities.size());
        return cities.get(indexCity);
    }

    public static String getRandomStreet() {
        var streets = List.of("Khreschatyk", "Shevchenka", "Francka",
                "Bohdana Khmelnytskogo", "Volodymyrska", "Mykhailivs'ka", "Lavrska",
                "Pecherska", "Zoloti Vorota", "Khreshchatyk", "Velyka Zhytomyrska",
                "Bratyska", "Dmytrivska", "Kalynivska", "Kyrylivska", "Lukyanivska", "Mezhyhirska");
        int indexStreet = (int) (Math.random() * streets.size());
        return streets.get(indexStreet);
    }

    public static String getRandomNumber() {
        var building = 1 + (int) Math.floor(Math.random() * 500);
        char block = (char) ('A' + Math.abs((int) (Math.random() * 26)));
        return String.format("%d-%c", building, block);
    }
}
