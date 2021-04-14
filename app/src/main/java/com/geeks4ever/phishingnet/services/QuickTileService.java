package com.geeks4ever.phishingnet.services;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.geeks4ever.phishingnet.model.repository.CommonRepository;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickTileService extends TileService {

    CommonRepository repository;
    boolean mainServiceOn = false;

    Tile tile;

    public QuickTileService(){

        repository = CommonRepository.getInstance(getBaseContext());
        if(getQsTile() == null)
            return;
        tile = getQsTile();
        mainServiceOn = getSharedPreferences("mainService", 0).getBoolean("mainService", false);
        tile.setState((mainServiceOn)?Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        if(getQsTile() == null)
            return;
        tile = getQsTile();
        mainServiceOn = getSharedPreferences("mainService", 0).getBoolean("mainService", false);
        tile.setState((mainServiceOn)?Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        tile = getQsTile();
        tile.setState((mainServiceOn)?Tile.STATE_INACTIVE : Tile.STATE_ACTIVE );
        tile.updateTile();
        repository.toggleMainServiceOnOff(!mainServiceOn);

    }
}
