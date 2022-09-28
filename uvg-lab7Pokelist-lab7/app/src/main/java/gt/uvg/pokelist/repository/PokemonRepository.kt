package gt.uvg.pokelist.repository

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import gt.uvg.pokelist.model.GetFirst100PokemonResponse
import gt.uvg.pokelist.model.PokeAPIService
import gt.uvg.pokelist.model.Pokemon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PokemonRepository {
    private val pokemonList = mutableListOf<Pokemon>(Pokemon(1, "bulbasaur"))
    fun getPokemonList(): List<Pokemon>{
        // CREATING THE POKEMON LIST THROUGH A CALL TO THE POKEAPI
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val pokeService: PokeAPIService = retrofit.create(PokeAPIService::class.java)

        if(pokemonList.size == 1){
            pokeService.getFirst100Pokemon().enqueue(object: Callback<GetFirst100PokemonResponse> {
                override fun onResponse(call: Call<GetFirst100PokemonResponse>, response: Response<GetFirst100PokemonResponse>) {
                    Log.i("RESPONSE DEL API", response.toString())

                    if(!response.isSuccessful){ return }

                    val body = response.body()!!
                    var i: Int = 1
                    pokemonList.removeAt(0)
                    for(pokemon in body.results) {
                        pokemonList.add(Pokemon(i++, pokemon.name))
                    }
                }

                override fun onFailure(call: Call<GetFirst100PokemonResponse>, t: Throwable) {
                    Log.i("MainFragment", t.message ?: "Null Message")
                }
            })
        }

        return pokemonList
    }
}