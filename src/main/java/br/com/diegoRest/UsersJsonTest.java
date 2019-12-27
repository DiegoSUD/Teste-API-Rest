package br.com.diegoRest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
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
	
	@Test
	public void deveFazerVerificacaoAvancada() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2)) // findAll aqui é um metodo para iteraração e é delimitado entre chaves
			.body("age.findAll{it > 20 && it <= 25}.size()", is(1))
			.body("findAll{it.age > 20 && it.age <= 25}.name", hasItem("Maria Joaquina"))
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) // Essa é forma que se consegue transformar a lista em um objeto podendo usar o is
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))  // Se quiser o ultimo registro coloca-se no indice -1 pq ele começa debaixo para cima 
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))  // O find retorna apenas um objeto
			
			//OBS: TODAS ESSAS ANOTAÇÕES SÃO BASEADAS NO GRUV
			
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina")) // usa o length() para seber o numero de caracteres por exemplo
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA")) // metodo que itera pela lista e faz uma modifição em cima dela 
			
//			.body("findAll{it.name}.name.collect{it.toUpperCase()}", hasItems("JOÃO DA SILVA", "MARIA JOAQUINA", "ANA JÚLIA"))
//			.body("name.collect{it.toUpperCase()}", hasItems("JOÃO DA SILVA", "MARIA JOAQUINA", "ANA JÚLIA")) 
			
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
		;
	}
	
	@Test
	public void devoUnirJsonPathComJava() {
		ArrayList<String> names = 
			given()
			.when()
				.get("http://restapi.wcaquino.me/users")
			.then()
				.statusCode(200)
				.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
				.extract().path("name.findAll{it.startsWith('Maria')}") // Essa extração retorna uma lista de String
			; 
		
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(names.get(0).toUpperCase(), "Maria Joaquina".toUpperCase());
	}
}
