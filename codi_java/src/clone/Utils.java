package clone;

// Aquesta classe conte funcions estatiques d'utilitat, que no
// conve incoure en cap altra classe per la seva generalitat.
// Son estatiques per poder-les invocar sense necessitat de crear
// un objecte.
public final class Utils {

	// Converteix un sencer no negatiu a representacio binari.
	// El bit mes significatiu es el de mes a l'esquerra, per tant
	// el primer element de l'array que els conte.
	// El valor de cada bit es representa amb true i false, no
	// amb "1" i "0".
	// Esta agafada del capitol d'operadors del Thinking in Java
	// de B. Eckel (cap. 3, pag. 151 2a edicio)
	public static boolean[] int2Binary(int decimalValue, int numBits) {
		boolean[] binaryValue = new boolean[numBits];
		for (int j=numBits-1 ; j>=0 ; j--) {
			if (((1 << j) & decimalValue) != 0) {
				binaryValue[numBits-j-1] = true ;
			} else {
				binaryValue[numBits-j-1] = false ;
			}	
		}
		return binaryValue;
	}

	// Funcio inversa a int2Binary.
	public static int binary2Int(boolean[] binaryValue) {
		int n = binaryValue.length;
		int res = 0;
		int power = 1;
		for (int i=n-1; i>=0; i--) {
			res += power*(binaryValue[i]?1:0);
			power *= 2 ;
		}
		return res ;
	}
}
