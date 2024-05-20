package code;

class Persona {

    private String persona_id;
    private String nombre;
    private int edad;

    public String getId(){
        return persona_id;
    }

    public void setId(String id){
        this.persona_id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Persona(){

    }

    public Persona(String id, String nombre, int edad) {
        this.persona_id = id;
        this.nombre = nombre;
        this.edad = edad;
    }
}
