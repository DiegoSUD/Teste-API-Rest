package br.com.diegoRest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.sun.org.glassfish.gmbal.ManagedAttribute;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		System.out.println(response.getBody().asString().equals("Ola Mundo!"));
		System.out.println(response.getStatusCode() == 200);

		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void devoConhecerOutrasFormasTestAssured() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		get("http://restapi.wcaquino.me/ola").then().statusCode(200);

		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200);
	}
	
	@Test
	public void devoConhecerMatcherHamcrest() {
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		Assert.assertThat(1278, Matchers.isA(Integer.class));
		Assert.assertThat(1278d, Matchers.isA(Double.class));
		Assert.assertThat(1278d, Matchers.greaterThan(1270d)); // greaterThan = maior que
		Assert.assertThat(1278, Matchers.lessThan(1280)); // lessThan = menor que
		
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, hasSize(5)); // se o tamanho dessa coleção é 5
		assertThat(impares, contains(1,3,5,7,9)); // para verificar todos os elementos da lista em ordem
		assertThat(impares, containsInAnyOrder(1,3,7,5,9)); // para verificar todos os elementos da lista não precisando estar em ordem
		assertThat(impares, containsInAnyOrder(1,3,7,5,9)); // para verificar todos os elementos da lista não precisando estar em ordem
		assertThat(impares, hasItem(9)); // para ver se tem um item na lista
		assertThat(impares, hasItems(1,5)); // para verificar se tem mais de um item na lista
		
		// Matchers Aninhados
		
		assertThat("Maria", is(not("joao"))); // O "is " serve para igualdade como também conectores de frases e ele é opcional
		assertThat("Maria" , not("joao")); // funciona da mesma forma sem o is
		assertThat("Maria", anyOf(is("Maria"), is("joaquina"))); // seja qualquer uma dessas opções que passar 
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui"))); // Condições em que todos opções que proceder como se fosse o &&.																								// containsString = pegar em qualquer parte que contenha o que foi passado pelo parametro.
	}
	
	
	@Test
	public void deveValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())))
			;
	}	
	
	

}
