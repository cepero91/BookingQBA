package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepoImpl;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.drawtype.DrawTypeRepoImpl;
import com.infinitum.bookingqba.model.repository.drawtype.DrawTypeRepository;
import com.infinitum.bookingqba.model.repository.galerie.GalerieRepoImpl;
import com.infinitum.bookingqba.model.repository.galerie.GalerieRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepoImpl;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.poi.PoiRepoImpl;
import com.infinitum.bookingqba.model.repository.poi.PoiRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepoImpl;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepoImpl;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepoImpl;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepoImpl;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.model.repository.rentamenities.RentAmenitiesRepoImpl;
import com.infinitum.bookingqba.model.repository.rentamenities.RentAmenitiesRepository;
import com.infinitum.bookingqba.model.repository.rentdrawtype.RentDrawTypeRepoImpl;
import com.infinitum.bookingqba.model.repository.rentdrawtype.RentDrawTypeRepository;
import com.infinitum.bookingqba.model.repository.rentmode.RentModeRepoImpl;
import com.infinitum.bookingqba.model.repository.rentmode.RentModeRepository;
import com.infinitum.bookingqba.model.repository.rentpoi.RentPoiRepoImpl;
import com.infinitum.bookingqba.model.repository.rentpoi.RentPoiRepository;
import com.infinitum.bookingqba.model.repository.rentvisitcount.RentVisitCountImpl;
import com.infinitum.bookingqba.model.repository.rentvisitcount.RentVisitCountRepository;
import com.infinitum.bookingqba.model.repository.user.UserRepoImp;
import com.infinitum.bookingqba.model.repository.user.UserRepository;

import dagger.Binds;
import dagger.Module;

@Module
public interface RepositoryModule {

    @Binds
    UserRepository userRepository(UserRepoImp userRepo);

    @Binds
    ProvinceRepository provinceRepository(ProvinceRepoImpl provinceRepo);

    @Binds
    MunicipalityRepository municipalityRepository(MunicipalityRepoImpl municipalityRepo);

    @Binds
    AmenitiesRepository amenitiesRepository(AmenitiesRepoImpl amenitiesRepo);

    @Binds
    PoiTypeRepository poiTypeRepository(PoiTypeRepoImpl poiTypeRepo);

    @Binds
    PoiRepository poiRepository(PoiRepoImpl poiRepo);

    @Binds
    RentRepository rentRepository(RentRepoImpl rentRepo);

    @Binds
    RentModeRepository rentModeRepository(RentModeRepoImpl rentModeRepo);

    @Binds
    ReferenceZoneRepository referenceZoneRepository(ReferenceZoneRepoImpl referenceZoneRepo);

    @Binds
    DrawTypeRepository drawTypeRepository(DrawTypeRepoImpl drawTypeRepo);

    @Binds
    GalerieRepository galerieRepository(GalerieRepoImpl galerieRepo);

    @Binds
    RentPoiRepository rentPoiRepository(RentPoiRepoImpl rentPoiRepo);

    @Binds
    RentDrawTypeRepository rentDrawTypeRepository(RentDrawTypeRepoImpl rentDrawTypeRepo);

    @Binds
    RentAmenitiesRepository rentAmenitiesRepository(RentAmenitiesRepoImpl rentAmenitiesRepo);

    @Binds
    RentVisitCountRepository rentVisitCountRepository(RentVisitCountImpl rentVisitCountRepo);

}
