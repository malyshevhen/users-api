package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
)

type User struct {
	Email     string  `json:"email"`
	FirstName string  `json:"firstName"`
	LastName  string  `json:"lastName"`
	BirthDate string  `json:"birthDate"`
	Address   Address `json:"address"`
	Phone     string  `json:"phone"`
}

type Address struct {
	Country string `json:"country"`
	City    string `json:"city"`
	Street  string `json:"street"`
	Number  string `json:"number"`
}

func Unmarshal[T any](v []byte) (value T, err error) {
	if err := json.Unmarshal(v, &value); err != nil {
		return value, errors.New("error parse JSON payload")
	}
	return
}

func main() {

	client := &http.Client{}
	url := "http://localhost:8080/api/users"
	method := "POST"

	data, err := os.ReadFile("data.json")
	if err != nil {
		log.Fatal("Error occured while read file: ", err)
	}

	users, err := Unmarshal[[]User](data)
	if err != nil {
		log.Fatal("Error occurred while Unmarshal json: ", err)
	}

	for _, u := range users {
		payload, err := json.Marshal(u)
		if err != nil {
			log.Println("Error Marshall User:", err.Error())
		}
		req, err := http.NewRequest(method, url, bytes.NewReader(payload))
		if err != nil {
			fmt.Println(err)
		}
		req.Header.Add("Content-Type", "application/json")
		req.Header.Add("Accept", "application/json")

		res, err := client.Do(req)
		if err != nil {
			fmt.Println(err)
			return
		}
		defer res.Body.Close()

		body, err := io.ReadAll(res.Body)
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(string(body))
	}
}
