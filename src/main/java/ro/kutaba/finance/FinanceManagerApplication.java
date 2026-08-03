package ro.kutaba.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceManagerApplication {

	public static void main(String[] args) {

		System.out.println("========== VERSION 2 ==========");
		SpringApplication.run(FinanceManagerApplication.class, args);
	}

}
