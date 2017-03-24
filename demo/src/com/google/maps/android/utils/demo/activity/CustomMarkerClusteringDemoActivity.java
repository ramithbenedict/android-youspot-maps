package com.google.maps.android.utils.demo.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.utils.demo.R;
import com.google.maps.android.utils.demo.model.Salle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomMarkerClusteringDemoActivity extends BaseDemoActivity implements ClusterManager.OnClusterClickListener<Salle>, ClusterManager.OnClusterInfoWindowClickListener<Salle>, ClusterManager.OnClusterItemClickListener<Salle>, ClusterManager.OnClusterItemInfoWindowClickListener<Salle> {
    private ClusterManager<Salle> mClusterManager;
    private Random mRandom = new Random(1984);


    private class SalleRenderer extends DefaultClusterRenderer<Salle> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public SalleRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Salle person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Salle> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Salle p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Salle> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Salle> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Salle item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Salle item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    @Override
    protected void startDemo() {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.71300000,2.57927000), 9.5f));

        mClusterManager = new ClusterManager<Salle>(this, getMap());
        mClusterManager.setRenderer(new SalleRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {
        mClusterManager.addItem(new Salle(position(48.85497000,2.59654000),"Gymnase Jean Jaures - Gymnase",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.85490000,2.58873000),"Gymnase Jean Jaures - Salle D'Arts Martiaux",R.drawable.dojosalledartsmartiaux));
        mClusterManager.addItem(new Salle(position(48.85490000,2.58873000),"Gymnase Jean Jaures - Salle De Danse",R.drawable.sallededanse));
        mClusterManager.addItem(new Salle(position(48.85535000,2.58806000),"Stade Lionel Hurtebize - Terrain De Football D'Honneur",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85441000,2.58764000),"Stade Lionel Hurtebize - Terrain De Football N°2",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85384000,2.58914000),"Stade Lionel Hurtebize - Terrain De Football N°3",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85540000,2.58827000),"Stade Lionel Hurtebize - Terrain De Petanque",R.drawable.terraindepetanque));
        mClusterManager.addItem(new Salle(position(48.85540000,2.58827000),"Stade Lionel Hurtebize - Stade D'Athletisme",R.drawable.stadedathletisme));
        mClusterManager.addItem(new Salle(position(48.85540000,2.58827000),"Stade Lionel Hurtebize - Salle De Preparation Physique",R.drawable.salledemusculationcardiotraining));
        mClusterManager.addItem(new Salle(position(48.85104000,2.58517000),"Complexe Des Pyramides - Gymnase",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.85130000,2.58639000),"Complexe Des Pyramides - Salle De Gymnastique - Agres",R.drawable.salledegymnastiquesportive));
        mClusterManager.addItem(new Salle(position(48.85130000,2.58639000),"Complexe Des Pyramides - Salle De Gymnastique Sol",R.drawable.salledegymnastiquesportive));
        mClusterManager.addItem(new Salle(position(48.85130000,2.58639000),"Complexe Des Pyramides - Plateau Eps 1",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.85130000,2.58639000),"Complexe Des Pyramides - Plateau Eps 2",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.85134000,2.58653000),"Complexe Des Pyramides - Terrain De Football Stabilise",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85130000,2.58639000),"Complexe Des Pyramides - Salle De Danse",R.drawable.sallededanse));
        mClusterManager.addItem(new Salle(position(48.84909000,2.58162000),"Gymnase Du Nesles - Gymnase",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.84920000,2.58206000),"Gymnase Du Nesles - Salle De Judo",R.drawable.dojosalledartsmartiaux));
        mClusterManager.addItem(new Salle(position(48.84920000,2.58206000),"Gymnase Du Nesles - Plateau Eps",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.84760000,2.58631000),"Gymnase Du Bois De Grace - Gymnase",R.drawable.salledeboxe));
        mClusterManager.addItem(new Salle(position(48.84401000,2.58871000),"Gymnase Descartes - Gymnase",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.84020000,2.58629000),"Gymnase Descartes - Salle De Tennis De Table",R.drawable.salledetennisdetable));
        mClusterManager.addItem(new Salle(position(48.84020000,2.58629000),"Gymnase Descartes - Plateau Eps Specifique Basket",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.83840000,2.59878000),"Stade Du Bois De L'Etang - Terrain De Rugby",R.drawable.terrainderugby));
        mClusterManager.addItem(new Salle(position(48.83940000,2.59990000),"Stade Du Bois De L'Etang - Courts De Tennis",R.drawable.courtdetennis));
        mClusterManager.addItem(new Salle(position(48.84001000,2.60879000),"Gymnase Pablo Picasso - Gymnase",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.83870000,2.60467000),"Gymnase Pablo Picasso - Plateau Eps",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.83870000,2.60467000),"Gymnase Pablo Picasso - Athletisme",R.drawable.stadedathletisme));
        mClusterManager.addItem(new Salle(position(48.84711000,2.60562000),"Stade De La Fontaine Aux Coulons - Terrain De Football",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.84710000,2.60572000),"Stade De La Fontaine Aux Coulons - Court De Tennis Couverts",R.drawable.courtdetennis));
        mClusterManager.addItem(new Salle(position(48.84710000,2.60572000),"Stade De La Fontaine Aux Coulons - Court De Tennis Exterieur",R.drawable.courtdetennis));
        mClusterManager.addItem(new Salle(position(48.84710000,2.60572000),"Stade De La Fontaine Aux Coulons - Espace Roller Skate",R.drawable.skatepark));
        mClusterManager.addItem(new Salle(position(48.85220000,2.61243000),"Ensemble Sportif De Plein-Air Des Deux Parcs - Terrain De Football",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.85220000,2.61243000),"Ensemble Sportif De Plein-Air Des Deux Parcs - Terrain De Basket Ball",R.drawable.terraindebasketball));
        mClusterManager.addItem(new Salle(position(48.85220000,2.61243000),"Ensemble Sportif De Plein-Air Des Deux Parcs - Plateau Libre",R.drawable.plateauepsmultisportscitystades));
        mClusterManager.addItem(new Salle(position(48.84140000,2.60592000),"Espace De Proximite Ouvert Pablo Picasso - Terrain De Hand-Ball",R.drawable.terraindehandball));
        mClusterManager.addItem(new Salle(position(48.84140000,2.60592000),"Espace De Proximite Ouvert Pablo Picasso - Terrains De Basket-Ball",R.drawable.terraindebasketball));
        mClusterManager.addItem(new Salle(position(48.85141000,2.58333000),"Espace De Proximite Ouvert Joliot Curie - Terrain De Football A 7",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85250000,2.58325000),"Espace De Proximite Ouvert Joliot Curie - Terrain Mini Football",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.85250000,2.58325000),"Espace De Proximite Ouvert Joliot Curie - Terrain De Basket-Ball",R.drawable.terraindebasketball));
        mClusterManager.addItem(new Salle(position(48.85110000,2.58027000),"Espace De Proximite Ouvert Paul Langevin - Terrain De Mini Hand-Ball",R.drawable.terraindehandball));
        mClusterManager.addItem(new Salle(position(48.85110000,2.58027000),"Espace De Proximite Ouvert Paul Langevin - Terrain De Mini Basket-Ball",R.drawable.terraindebasketball));
        mClusterManager.addItem(new Salle(position(48.85110000,2.58027000),"Espace De Proximite Ouvert Paul Langevin - Terrain De Petanque",R.drawable.terraindepetanque));
        mClusterManager.addItem(new Salle(position(48.85140000,2.60388000),"Terrain De Tir A L'Arc - Terrain De Tir A L'Arc",R.drawable.pasdetiralarc));
        mClusterManager.addItem(new Salle(position(48.84120000,2.58815000),"Ecole Nationale Ponts Et Chaussees - Terrain De Basket Ball",R.drawable.terraindebasketball));
        mClusterManager.addItem(new Salle(position(48.84128000,2.59234000),"Ecole Nationale Ponts Et Chaussees - Salle Sportive",R.drawable.sallemultisports));
        mClusterManager.addItem(new Salle(position(48.84237000,2.58730000),"Ecole Nationale Ponts Et Chaussees - Terrain De Football - Rugby",R.drawable.terrainmixte));
        mClusterManager.addItem(new Salle(position(48.84120000,2.58815000),"Ecole Nationale Ponts Et Chaussees - Courts De Tennis",R.drawable.courtdetennis));
        mClusterManager.addItem(new Salle(position(48.85153000,2.59809000),"Terrain De Football - Terrain De Football",R.drawable.terraindefootball));
        mClusterManager.addItem(new Salle(position(48.86530000,2.59485000),"Base De Loisirs - Plan D'Eau",R.drawable.sitedactivitesaquatiquesetnautiques));
        mClusterManager.addItem(new Salle(position(48.86612000,2.59673000),"Base De Loisirs - Tir A L'Arc",R.drawable.pasdetiralarc));
        mClusterManager.addItem(new Salle(position(48.86559000,2.59553000),"Base De Loisirs - Trampoline",R.drawable.salledetrampoline));
        mClusterManager.addItem(new Salle(position(48.86633000,2.59761000),"Base De Loisirs - Terrain De Hockey / Roller",R.drawable.skatepark));
        mClusterManager.addItem(new Salle(position(48.86526000,2.59719000),"Bassin D'Entrainement De Canoe-Kayak Des Iles Mortes - Bassin De Slalom",R.drawable.stadedeauvive));
        mClusterManager.addItem(new Salle(position(48.85539000,2.58367000),"Association Billard De Champs Sur Marne - Salle De Billard N°1",R.drawable.salledebillard));
        mClusterManager.addItem(new Salle(position(48.85539000,2.58357000),"Association Billard De Champs Sur Marne - Salle De Billard N°2",R.drawable.salledebillard));
        mClusterManager.addItem(new Salle(position(48.84760000,2.58631000),"Gymnase Du Bois De Grace - Salle De Fitness Et Boxe",R.drawable.sallespolyvalentesdesfetesnonspecialisees));
        mClusterManager.addItem(new Salle(position(48.84760000,2.58631000),"Gymnase Du Bois De Grace - Salle De Boxe",R.drawable.salledeboxe));
        mClusterManager.addItem(new Salle(position(48.84020000,2.58629000),"Gymnase Descartes - Mur D'Escalade",R.drawable.structureartificielledescalade));
        mClusterManager.addItem(new Salle(position(48.84918500,2.58496400),"Caps Gym - Salle De Musculation",R.drawable.salledemusculationcardiotraining));

    }
    private LatLng position(double lat,double lng) {
        return new LatLng(lat,lng);
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
