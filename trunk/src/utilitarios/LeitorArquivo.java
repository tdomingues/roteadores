package utilitarios;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import excecoes.LeitorArquivosException;


public class LeitorArquivo {
	
	public LeitorArquivo() {
	}
	
	
	public static List<String> leiaArquivo(String nome) throws LeitorArquivosException {
		
		try {
			List<String> result = new ArrayList<String>();
			BufferedReader buffer = new BufferedReader(new FileReader(new File(nome)));
			String linha = buffer.readLine();
			while ( linha != null ) {
				if (!linha.startsWith("#") )
					result.add( linha );
				linha = buffer.readLine();
			}
			return result;
		} catch ( IOException ioe ) {
			throw new LeitorArquivosException ("Nao foi possivel ler o arquivo " + nome);
		}
		
	}

}
