package com.javatravel.interfaces;

import java.util.List;

public interface IGuardable<T> {
    boolean guardar(T entidad);
    boolean actualizar(T entidad);
    boolean eliminar(int id);
    T buscarPorId(int id);
    List<T> listarTodos();
}


