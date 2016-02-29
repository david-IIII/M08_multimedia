package david.juez.multimedia;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.Firebase;

/**
 * A placeholder fragment containing a simple view.
 */
public class FotoActivityFragment extends Fragment {
    private Button bt_hacerfoto;
    private Firebase fotos;
    private String fotopath;

    public FotoActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foto, container, false);

        //FIreBase
        Firebase.setAndroidContext(getContext());
        Firebase ref = new Firebase("https://uf2multimediadavid.firebaseio.com/");

        fotos = ref.child("fotos");

        bt_hacerfoto = (Button) view.findViewById(R.id.button1);

        //Añadimos el Listener Boton
        bt_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent para llamar a la Camara
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //Creamos una carpeta en la memoria del terminal
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "david");
                imagesFolder.mkdirs();
                //añadimos el nombre de la imagen
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "IMG_" + timeStamp + ".jpg";

                File image = new File( imagesFolder, imageFileName );
                fotopath = image.getAbsolutePath();
                Uri uriSavedImage = Uri.fromFile(image);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, 1);
            }
        });
        return view;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK ) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Foto foto = new Foto();
            foto.setRuta(fotopath);
            foto.setLat(location.getLatitude());
            foto.setLon(location.getLongitude());
            Firebase newNota = fotos.push();
            newNota.setValue(foto);
            getActivity().finish();
        }
    }
}