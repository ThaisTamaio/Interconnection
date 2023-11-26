package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.ArregloDinamico;
import model.data_structures.Country;
import model.data_structures.Country.ComparadorXKm;
import model.data_structures.Edge;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ILista;
import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;
import model.data_structures.ListaEncadenada;
import model.data_structures.NodoTS;
import model.data_structures.NullException;
import model.data_structures.PilaEncadenada;
import model.data_structures.PosException;
import model.data_structures.TablaHashLinearProbing;
import model.data_structures.TablaHashSeparteChaining;
import model.data_structures.VacioException;
import model.data_structures.Vertex;
import model.data_structures.YoutubeVideo;
import utils.Ordenamiento;


/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo {
	/**
	 * Atributos del modelo del mundo
	 */
	private ILista datos;
	
	private GrafoListaAdyacencia grafo;
	
	private ITablaSimbolos paises;
	
	private ITablaSimbolos points;
	
	private ITablaSimbolos landingidtabla;
	
	private ITablaSimbolos nombrecodigo;

	/**
	 * Constructor del modelo del mundo con capacidad dada
	 * @param tamano
	 */
	public Modelo(int capacidad)
	{
		datos = new ArregloDinamico<>(capacidad);
	}

	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo 
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamano()
	{
		return datos.size();
	}


	/**
	 * Requerimiento buscar dato
	 * @param dato Dato a buscar
	 * @return dato encontrado
	 * @throws VacioException 
	 * @throws PosException 
	 */
	public YoutubeVideo getElement(int i) throws PosException, VacioException
	{
		return (YoutubeVideo) datos.getElement( i);
	}

	public String toString()
	{
		String fragmento="Info básica:";
		
		fragmento+= "\n El número total de conexiones (arcos) en el grafo es: " + grafo.edges().size();
		fragmento+="\n El número total de puntos de conexión (landing points) en el grafo: " + grafo.vertices().size();
		fragmento+= "\n La cantidad total de países es:  " + paises.size();
		Landing landing=null;
		try 
		{
			landing = (Landing) ((NodoTS) points.darListaNodos().getElement(1)).getValue();
			fragmento+= "\n Info primer landing point " + "\n Identificador: " + landing.getId() + "\n Nombre: " + landing.getName()
			+ " \n Latitud " + landing.getLatitude() + " \n Longitud" + landing.getLongitude();
			
			Country pais= (Country) ((NodoTS) paises.darListaNodos().getElement(paises.darListaNodos().size())).getValue();
			
			fragmento+= "\n Info último país: " + "\n Capital: "+ pais.getCapitalName() + "\n Población: " + pais.getPopulation()+
			"\n Usuarios: "+ pais.getUsers();
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fragmento;
	}
	
	
	public String req1String(String punto1, String punto2)
	{
		ITablaSimbolos tabla= grafo.getSSC();
		ILista lista= tabla.valueSet();
		int max=0;
		for(int i=1; i<= lista.size(); i++)
		{
			try
			{
				if((int) lista.getElement(i)> max)
				{
					max= (int) lista.getElement(i);
				}
			}
			catch(PosException | VacioException  e)
			{
				System.out.println(e.toString());
			}
			
		}
		
		String fragmento="La cantidad de componentes conectados es: " + max;
		
		try 
		{
			String codigo1= (String) nombrecodigo.get(punto1);
			String codigo2= (String) nombrecodigo.get(punto2);
			Vertex vertice1= (Vertex) ((ILista) landingidtabla.get(codigo1)).getElement(1);
			Vertex vertice2= (Vertex) ((ILista) landingidtabla.get(codigo2)).getElement(1);
			
			int elemento1= (int) tabla.get(vertice1.getId());
			int elemento2= (int) tabla.get(vertice2.getId());
			
			if(elemento1== elemento2)
			{
				fragmento+= "\n Los landing points pertenecen al mismo clúster";
			}
			else
			{
				fragmento+= "\n Los landing points no pertenecen al mismo clúster";
			}
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return fragmento;
		
	}
	
	public String req2String()
	{
		String fragmento="";
		ILista lista= landingidtabla.valueSet();
		int cantidad=0;
		int contador=0;
		
		for(int i=1; i<= lista.size(); i++)
		{
			try 
			{
				if( ( (ILista) lista.getElement(i) ).size()>1 && contador<=10)
				{
					Landing landing= (Landing) ((Vertex) ((ILista) lista.getElement(i) ).getElement(1)).getInfo();
					
					for(int j=1; j<=((ILista) lista.getElement(i)).size(); j++)
					{
						cantidad+= ((Vertex) ((ILista) lista.getElement(i)).getElement(j)).edges().size();
					}
					fragmento+= "\n Landing " + "\n Nombre: " + landing.getName() + "\n País: " + landing.getPais() + "\n Id: " + landing.getId() + "\n Cantidad: " + cantidad;
					contador++;
				}
			}
			catch (PosException | VacioException e) 
			{
				e.printStackTrace();
			}
			
		}
		
		return fragmento;
		
	}
	
	public String req3String(String pais1, String pais2)
	{
		Country pais11= (Country) paises.get(pais1);
		Country pais22= (Country) paises.get(pais2);
		String capital1=pais11.getCapitalName();
		String capital2=pais22.getCapitalName();

		PilaEncadenada pila= grafo.minPath(capital1, capital2);

		float distancia=0;

		String fragmento="Ruta: ";

		float disttotal=0;
		
		double longorigen=0;
		double longdestino=0;
		double latorigen=0;
		double latdestino=0;
		String origennombre="";
		String destinonombre="";

		while(!pila.isEmpty())
		{
			Edge arco= ((Edge)pila.pop());

			if(arco.getSource().getInfo().getClass().getName().equals("model.data_structures.Landing"))
			{
				longorigen=((Landing)arco.getSource().getInfo()).getLongitude();
				latorigen=((Landing)arco.getSource().getInfo()).getLongitude();
				origennombre=((Landing)arco.getSource().getInfo()).getLandingId();
			}
			if(arco.getSource().getInfo().getClass().getName().equals("model.data_structures.Country"))
			{
				longorigen=((Country)arco.getSource().getInfo()).getLongitude();
				latorigen=((Country)arco.getSource().getInfo()).getLongitude();
				origennombre=((Country)arco.getSource().getInfo()).getCapitalName();
			}
			if (arco.getDestination().getInfo().getClass().getName().equals("model.data_structures.Landing"))
			{
				latdestino=((Landing)arco.getDestination().getInfo()).getLatitude();
				longdestino=((Landing)arco.getDestination().getInfo()).getLatitude();
				destinonombre=((Landing)arco.getDestination().getInfo()).getLandingId();
			}
			if(arco.getDestination().getInfo().getClass().getName().equals("model.data_structures.Country"))
			{
				longdestino=((Country)arco.getDestination().getInfo()).getLatitude();
				latdestino=((Country)arco.getDestination().getInfo()).getLatitude();
				destinonombre=((Country)arco.getDestination().getInfo()).getCapitalName();
			}

			distancia = distancia(longdestino,latdestino, longorigen, latorigen);
			fragmento+= "\n \n Origen: " +origennombre + "  Destino: " + destinonombre + "  Distancia: " + distancia;
			disttotal+=distancia;

		}

		fragmento+= "\n Distancia total: " + disttotal;	

		return fragmento;
		
	}
	
	public String req4String()
	{		
		try
		{
			ILista lista1 = landingidtabla.valueSet();
	        String llave = "";
	        int distancia = 0;
	        
	        String fragmento = obtenerFragmentoMasLargo(lista1, llave, distancia);
	        
	        if(fragmento.equals(""))
			{	
				return "No hay ninguna rama";
			}
			else 
			{
				return fragmento;
			}
		}
		catch (PosException | VacioException | NullException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
	}
	
	private String obtenerFragmentoMasLargo(ILista lista1, String llave, int distancia) throws PosException, VacioException, NullException {
		int max = 0;
	    
	    for (int i = 1; i <= lista1.size(); i++) {
	        ILista subLista = (ILista) lista1.getElement(i);
	        
	        if (subLista.size() > max) {
	            max = subLista.size();
	            llave = ((Vertex) subLista.getElement(1)).getId().toString();
	        }
	    }
	    
	    ILista lista2 = grafo.mstPrimLazy(llave);
		ITablaSimbolos tabla= new TablaHashSeparteChaining<>(2);
	    ILista candidatos = new ArregloDinamico<>(1);
	    
	    for (int i = 1; i <= lista2.size(); i++) {
	        Edge arco = (Edge) lista2.getElement(i);
	        distancia += arco.getWeight();
	        
	        candidatos.insertElement(arco.getSource(), candidatos.size() + 1);
	        candidatos.insertElement(arco.getDestination(), candidatos.size() + 1);
	        
	        tabla.put(arco.getDestination().getId(), arco.getSource());
	    }
	    
	    ILista unificado = unificar(candidatos, "Vertice");
	    String resultado = " La cantidad de nodos conectados a la red de expansión mínima es: " + unificado.size() + "\n El costo total es de: " + distancia;
	    
	    PilaEncadenada caminomax = obtenerRamaMasLarga(unificado, tabla);
	    resultado += "\n La rama más larga está dada por los vértices: " + obtenerVertices(caminomax);
	    
	    return resultado;
	}
	
	private PilaEncadenada obtenerRamaMasLarga(ILista unificado, ITablaSimbolos tabla) throws PosException, VacioException {
	    int maximo = 0;
	    PilaEncadenada caminomax = new PilaEncadenada();
	    
	    for (int i = 1; i <= unificado.size(); i++) {
	        PilaEncadenada path = new PilaEncadenada();
	        String idBusqueda = ((Vertex) unificado.getElement(i)).getId().toString();
	        Vertex actual;
	        int contador = 0;

	        while ((actual = (Vertex) tabla.get(idBusqueda)) != null && actual.getInfo() != null) {
	            path.push(actual);
	            idBusqueda = actual.getId().toString();
	            contador++;
	        }
	        
	        if (contador > maximo) {
	            caminomax = path;
	            maximo = contador;
	        }
	    }
	    
	    return caminomax;
	}

	private String obtenerVertices(PilaEncadenada pila) {
	    StringBuilder sb = new StringBuilder();
	    
	    while (!pila.isEmpty()) {
	        Vertex pop = (Vertex) pila.pop();
	        sb.append("\n Id: ").append(pop.getId());
	    }
	    
	    return sb.toString();
	}
	
	public ILista req5(String punto) {
	    String codigo = (String) nombrecodigo.get(punto);
	    ILista lista = (ILista) landingidtabla.get(codigo);

	    ILista countries = new ArregloDinamico<>(1);

	    try {
	        agregarPaisOriginal(lista, countries);
	    } catch (PosException | VacioException | NullException e) {
	        e.printStackTrace();
	    }

	    for (int i = 1; i <= lista.size(); i++) {
	        try {
	            Vertex vertice = (Vertex) lista.getElement(i);
	            procesarArcos(vertice, countries);

	        } catch (PosException | VacioException | NullException e) {
	            e.printStackTrace();
	        }
	    }

	    ILista unificado = unificar(countries, "Country");

	    ordenarPorDistancia(unificado);

	    return unificado;
	}

	private void agregarPaisOriginal(ILista lista, ILista countries) throws PosException, VacioException, NullException {
	    Country paisOriginal = obtenerPaisOriginal(lista);
	    countries.insertElement(paisOriginal, countries.size() + 1);
	}

	private Country obtenerPaisOriginal(ILista lista) throws PosException, VacioException, NullException {
	    Vertex primerVertice = (Vertex) lista.getElement(1);
	    Landing landing = (Landing) primerVertice.getInfo();
	    return (Country) paises.get(landing.getPais());
	}

	private void procesarArcos(Vertex vertice, ILista countries) throws PosException, VacioException, NullException {
	    ILista arcos = vertice.edges();

	    for (int j = 1; j <= arcos.size(); j++) {
	        Vertex vertice2 = ((Edge) arcos.getElement(j)).getDestination();
	        Country pais = obtenerPaisDesdeVertice(vertice2);

	        countries.insertElement(pais, countries.size() + 1);

	        if (vertice2.getInfo().getClass().getName().equals("model.data_structures.Landing")) {
	            procesarDistancia(pais, (Landing) vertice2.getInfo());
	        }
	    }
	}

	private Country obtenerPaisDesdeVertice(Vertex vertice) {
	    if (vertice.getInfo().getClass().getName().equals("model.data_structures.Landing")) {
	        Landing landing = (Landing) vertice.getInfo();
	        return (Country) paises.get(landing.getPais());
	    } else {
	        return (Country) vertice.getInfo();
	    }
	}

	private void procesarDistancia(Country pais, Landing landing) {
	    float distancia = distancia(pais.getLongitude(), pais.getLatitude(), landing.getLongitude(), landing.getLatitude());
	    pais.setDistlan(distancia);
	}
	
	private void ordenarPorDistancia(ILista unificado) {
	    Comparator<Country> comparador = new ComparadorXKm();
	    Ordenamiento<Country> algsOrdenamientoEventos = new Ordenamiento<>();

	    try {
	        if (unificado != null) {
	            algsOrdenamientoEventos.ordenarMergeSort(unificado, comparador, true);
	        }
	    } catch (PosException | VacioException | NullException e) {
	        e.printStackTrace();
	    }
	}
	
	public String req5String(String punto)
	{
		ILista afectados= req5(punto);
		
		String fragmento="La cantidad de paises afectados es: " + afectados.size() + "\n Los paises afectados son: ";
	
		for(int i=1; i<=afectados.size(); i++)
		{
			try {
				fragmento+= "\n Nombre: " + ((Country) afectados.getElement(i)).getCountryName() + "\n Distancia al landing point: " + ((Country) afectados.getElement(i)).getDistlan();
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return fragmento;
		
		
	}
	
	public ILista unificar(ILista lista, String criterio) {
	    ILista lista2 = new ArregloDinamico(1);

	    if (criterio.equals("Vertice")) {
	        compararYAgregarVertices(lista, lista2);
	    } else {
	        compararYAgregarPaises(lista, lista2);
	    }

	    return lista2;
	}
	
	private void compararYAgregarVertices(ILista lista, ILista lista2) {
		Comparator<Vertex<String, Landing>> comparador=null;
		Ordenamiento<Vertex<String, Landing>> algsOrdenamientoEventos=new Ordenamiento<Vertex<String, Landing>>();
		comparador= new Vertex.ComparadorXKey();
		try 
		{
			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);

				for(int i=1; i<=lista.size(); i++)
				{
					Vertex actual= (Vertex) lista.getElement(i);
					Vertex siguiente= (Vertex) lista.getElement(i+1);

					if(siguiente!=null)
					{
						if(comparador.compare(actual, siguiente)!=0)
						{
							lista2.insertElement(actual, lista2.size()+1);
						}
					}
					else
					{
						Vertex anterior= (Vertex) lista.getElement(i-1);
						if(anterior!=null)
						{
							if(comparador.compare(anterior, actual)!=0)
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}
						else
						{
							lista2.insertElement(actual, lista2.size()+1);
						}
					}
				}
			}
		} 
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void compararYAgregarPaises(ILista lista, ILista lista2) {
		Comparator<Country> comparador=null;
		Ordenamiento<Country> algsOrdenamientoEventos=new Ordenamiento<Country>();
		comparador= new Country.ComparadorXNombre();
		try 
		{
			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);
			}
				for(int i=1; i<=lista.size(); i++)
				{
					Country actual= (Country) lista.getElement(i);
					Country siguiente= (Country) lista.getElement(i+1);

					if(siguiente!=null)
					{
						if(comparador.compare(actual, siguiente)!=0)
						{
							lista2.insertElement(actual, lista2.size()+1);
						}
					}
					else
					{
						Country anterior= (Country) lista.getElement(i-1);
						if(anterior!=null)
						{
							if(comparador.compare(anterior, actual)!=0)
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}
						else
						{
							lista2.insertElement(actual, lista2.size()+1);
						}
					}

				}
			}
		
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cargar() throws IOException {
	    inicializarEstructuras();
	    procesarPaises();
	    procesarPuntosDeAterrizaje();
	    procesarConexiones();
	    establecerConexionesEntreVertices();
	}
	
	private void inicializarEstructuras() {
	    grafo = new GrafoListaAdyacencia(2);
	    paises = new TablaHashLinearProbing(2);
	    points = new TablaHashLinearProbing(2);
	    landingidtabla = new TablaHashSeparteChaining(2);
	    nombrecodigo = new TablaHashSeparteChaining(2);
	}
	
	private void procesarPaises() throws IOException {
		Reader in = new FileReader("./data/countries.csv");
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
		int contador=1;
		for (CSVRecord record : records) 
		{
			if(!record.get(0).equals(""))
			{
				String countryName= record.get(0);
				String capitalName= record.get(1);
				
				double latitude= Double.parseDouble(record.get(2));
				double longitude= Double.parseDouble(record.get(3));
				
				String code= record.get(4);
				String continentName= record.get(5);
				
				float population= Float.parseFloat(record.get(6).replace(".", ""));
				double users= Double.parseDouble(record.get(7).replace(".", ""));;
				Country pais= new Country(countryName, capitalName, latitude, longitude, code, continentName, population, users);
				
				grafo.insertVertex(capitalName, pais);
				paises.put(countryName, pais);

				contador++;
			}
		}
	}

	private void procesarPuntosDeAterrizaje() throws IOException {
		Reader in2 = new FileReader("./data/landing_points.csv");
		Iterable<CSVRecord> records2 = CSVFormat.RFC4180.withHeader().parse(in2);

		int contador2=1;
		
		for (CSVRecord record2 : records2) 
		{

			String landingId= record2.get(0);
			
			String id=record2.get(1);
			
			String[] x= record2.get(2).split(", ");
			
			String name= x[0];
			
			String paisnombre= x[x.length-1];
			
			double latitude= Double.parseDouble(record2.get(3));
			
			double longitude= Double.parseDouble(record2.get(4));
			
			Landing landing= new Landing(landingId, id, name, paisnombre, latitude, longitude);
			
			points.put(landingId, landing);
			
			Country pais= null;
		}
	}
	
	private void procesarConexiones() throws IOException {
	    Reader in3 = new FileReader("./data/connections.csv");
	    Iterable<CSVRecord> records3 = CSVFormat.RFC4180.withHeader().parse(in3);

	    for (CSVRecord record3 : records3) {
	        procesarConexion(record3);
	    }
	}
	
	private void procesarConexion(CSVRecord record) {
		
		String origin= record.get(0);
		String destination= record.get(1);
		String cableid= record.get(3);

		Landing landing1= (Landing) points.get(origin);
		grafo.insertVertex(landing1.getLandingId()+ cableid, landing1);
		Landing landing2= (Landing) points.get(destination);
		grafo.insertVertex(landing2.getLandingId()+ cableid, landing2);
		
		if (landing1 != null && landing2 != null) {
			procesarPaisesYVertices(landing1, landing2, cableid);		
			procesarAristasYDistancias(landing1, landing2, cableid);
			procesarElementoTabla(landing1, landing2, cableid);
		}
	}
	
	private void procesarAristasYDistancias(Landing landing1, Landing landing2, String cableId) {
	    Edge existe1 = grafo.getEdge(landing1.getLandingId() + cableId, landing2.getLandingId() + cableId);

	    if (existe1 == null) {
	        float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(), landing2.getLongitude(), landing2.getLatitude());
	        grafo.addEdge(landing1.getLandingId() + cableId, landing2.getLandingId() + cableId, weight3);
	    } else {
	        float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(), landing2.getLongitude(), landing2.getLatitude());
	        float peso3 = existe1.getWeight();

	        if (weight3 > peso3) {
	            existe1.setWeight(weight3);
	        }
	    }
	}
	
	private void procesarPaisesYVertices(Landing landing1, Landing landing2, String cableId) {
	    String nombrePais1 = landing1.getPais();
	    String nombrePais2 = landing2.getPais();

	    Country pais1 = obtenerPais(nombrePais1);
	    Country pais2 = obtenerPais(nombrePais2);

	    if (pais1 != null) {
	        float weight = distancia(pais1.getLongitude(), pais1.getLatitude(), landing1.getLongitude(), landing1.getLatitude());
	        grafo.addEdge(pais1.getCapitalName(), landing1.getLandingId() + cableId, weight);
	    }

	    if (pais2 != null) {
	        float weight2 = distancia(pais2.getLongitude(), pais2.getLatitude(), landing1.getLongitude(), landing1.getLatitude());
	        grafo.addEdge(pais2.getCapitalName(), landing2.getLandingId() + cableId, weight2);
	    }
	}
	
	private Country obtenerPais(String nombrePais) {
	    if ("Côte d'Ivoire".equals(nombrePais)) {
	        return (Country) paises.get("Cote d'Ivoire");
	    } else {
	        return (Country) paises.get(nombrePais);
	    }
	}
	
	private void procesarElementoTabla(Landing landing1, Landing landing2, String cableid) {
		try
		{
			Vertex vertice1= grafo.getVertex(landing1.getLandingId()+ cableid);
			
			Vertex vertice2= grafo.getVertex(landing2.getLandingId()+ cableid);
			
			ILista elementopc= (ILista) landingidtabla.get(landing1.getLandingId());
			if (elementopc==null)
			{
				ILista valores=new ArregloDinamico(1);
				valores.insertElement(vertice1, valores.size() +1);

				landingidtabla.put(landing1.getLandingId(), valores);
				
			}
			else if (elementopc!=null)
			{
				elementopc.insertElement(vertice1, elementopc.size()+1);
			}
		
			elementopc= (ILista) landingidtabla.get(landing2.getLandingId());

			if (elementopc==null)
			{
				ILista valores=new ArregloDinamico(1);
				valores.insertElement(vertice2, valores.size() +1);

				landingidtabla.put(landing2.getLandingId(), valores);

			}
			else if (elementopc!=null)
			{
				elementopc.insertElement(vertice2, elementopc.size()+1);

			}
			
			elementopc= (ILista) nombrecodigo.get(landing1.getLandingId());
			
			if (elementopc==null)
			{
				String nombre=landing1.getName();
				String codigo=landing1.getLandingId();

				nombrecodigo.put(nombre, codigo);

			}
		}
		catch(PosException | NullException e)
		{
			e.printStackTrace();
		}
	}
	
	private void establecerConexionesEntreVertices() {
		try
		{
			ILista valores = landingidtabla.valueSet();
			
			for(int i=1; i<=valores.size(); i++)
			{
				for(int j=1; j<=((ILista) valores.getElement(i)).size(); j++)
				{
					Vertex vertice1;
					if((ILista) valores.getElement(i) != null)
					{
						vertice1= (Vertex) ((ILista) valores.getElement(i)).getElement(j);
						for(int k=2; k<= ((ILista) valores.getElement(i)).size(); k++)
						{
							Vertex vertice2= (Vertex) ((ILista) valores.getElement(i)).getElement(k);
							grafo.addEdge(vertice1.getId(), vertice2.getId(), 100);
						}
					}
				}
			}
		}
		catch(PosException | VacioException  e)
		{
			e.printStackTrace();
		}
	}

	private static float distancia(double lon1, double lat1, double lon2, double lat2) 
	{

		double earthRadius = 6371; // km

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = (lon2 - lon1);
		double dlat = (lat2 - lat1);

		double sinlat = Math.sin(dlat / 2);
		double sinlon = Math.sin(dlon / 2);

		double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
		double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));

		double distance = earthRadius * c;

		return (int) distance;

	}
}
