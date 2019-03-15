package com.infinitum.bookingqba.di.module;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.viewmodel.HomeViewModel;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Module encargado de proveer los viewModel de la aplicacion
 */
@Module
public interface ViewModelModule {

    /**
     * La annotation {@link Target} es usada para indicar donde se va a usar la annotation creada.
     * Si no la pongo no da error pero se recomienda por que asi asegurar que se use en los lugares
     * adecuados.
     * La annotation {@link Retention} se usa para especificar la politica de retencion de la
     * annotation,es especificada por que por defecto es RetentionPolicy.CLASS y esta no es
     * accesible en tiempo de ejecucion.
     * La annotation {@link MapKey} es la encargada de crear una nueva annotation
     *
     * @{@link ViewModelKey} para mapear los objectos proporcionados por Dagger.De esta forma es
     * muy facil agregar nuevos ViewModel q necesiten ser inyectados porque simplemente se anota
     * el provide con la annotation creada pasandole el ViewModel.Si no usamos esta solucion
     * (multibindings) tendriamos que pasarle a nuestro ViewModelFactory en el contructor todos
     * los ViewModel que vamos a usar y esto haria que la clase no fuera escalable.
     */
    @Documented
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    /**
     * La annotation {@link IntoMap} es necesaria para indicar que se inyecte este object dentro del
     * map que tiene como key el valor pasado a @{@link ViewModelKey} y como valor el return de la
     * annotation @{@link Binds}
     *
     * @param syncViewModel Se especifica el viewModel a inyectar
     * @return el viewModel correspondiente
     */
    @IntoMap
    @Binds
    @ViewModelKey(SyncViewModel.class)
    ViewModel syncViewModel(SyncViewModel syncViewModel);

    @IntoMap
    @Binds
    @ViewModelKey(HomeViewModel.class)
    ViewModel homeViewModel(HomeViewModel homeViewModel);

    @IntoMap
    @Binds
    @ViewModelKey(RentViewModel.class)
    ViewModel rentViewModel(RentViewModel rentViewModel);


}
