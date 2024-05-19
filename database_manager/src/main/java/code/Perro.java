package code;

public class Perro {

    private String raza;
    private String nombre;
    private String comidaFavorita;
    private int edad;
    
    public Perro(String raza, String nombre, String comidaFavorita, int edad) {
        this.raza = raza;
        this.nombre = nombre;
        this.comidaFavorita = comidaFavorita;
        this.edad = edad;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComidaFavorita() {
        return comidaFavorita;
    }

    public void setComidaFavorita(String comidaFavorita) {
        this.comidaFavorita = comidaFavorita;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
