package com.torneo.model;

public class BracketNode {
    private final String idNodo;
    private Partida partida;
    private BracketNode izquierdo;
    private BracketNode derecho;
    private BracketNode siguiente;

    public BracketNode(String idNodo, Partida partida) {
        this.idNodo = idNodo;
        this.partida = partida;
    }

    public String getIdNodo() { return idNodo; }
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    public BracketNode getIzquierdo() { return izquierdo; }
    public void setIzquierdo(BracketNode izquierdo) { this.izquierdo = izquierdo; }
    public BracketNode getDerecho() { return derecho; }
    public void setDerecho(BracketNode derecho) { this.derecho = derecho; }
    public BracketNode getSiguiente() { return siguiente; }
    public void setSiguiente(BracketNode siguiente) { this.siguiente = siguiente; }
}