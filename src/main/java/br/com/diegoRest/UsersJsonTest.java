package br.com.diegoRest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class UsersJsonTest {
	
	@Test
	public void deveVerificarPrimeiroNivelJson() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
		;
		
	}
	
	@Test
	public void deveVerificarOutroNivelJsonOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1" );
		
		//PATH
		assertEquals(1, response.path("id"));
		assertEquals(1, response.path("%s", "id" )); //Passando por parâmetro
		
		//JsonPath
		JsonPath jpath = new JsonPath(response.asString());		
		assertEquals(1, jpath.getInt("id"));
		
		//From que é feito de uma forma estatica
		
		int id = JsonPath.from(response.asString()).getInt("id");
		assertEquals(1, id);
	}
	
	@Test
	public void deveVerificarSegundoNivelJson() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"))  // se tivesse mais coisas no segundo nivel poderia ser usado EX: enderco.rua.numero etc
		;
			
	}
	
	@Test	
	public void deveVerificarUmaListaEmJson() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("ana", containsString("ana"))
			.body("filhos", hasSize(2))  // se começa pela chave inicial
			.body("filhos [0]. name", is("Zezinho"))  // No caso de uma arrayList por ser um vetor pode se tratar com indice 
			.body("filhos [1].name", is("Luizinho"))  // ou
			.body("filhos.name", hasItem("Luizinho")) // pode ser feita dessa forma 
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))	// ou dessa 
		;
	}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	} 
	
	
	//TRABALHANDO COM LISTA NA RAIZ
	
	@Test
	public void deveVerificarListaRaiz() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))  // Quando se a lista raiz e nao tem chave usa-se qualquer coisa até mesmo em branco irá funcionar
			.body("name", hasItems("João da Silva", "Maria Joaquina" , "Ana Júlia"))
			.body("age[1]", is(25)) 
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho"))) // nesse caso é uma lista dentro de outra lista
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
}
