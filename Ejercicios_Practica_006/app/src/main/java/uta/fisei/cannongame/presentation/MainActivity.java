// MainActivity.java
// MainActivity displays the MainActivityFragment
package uta.fisei.cannongame.presentation;

import uta.fisei.cannongame.R;

import uta.fisei.cannongame.domain.entities.Blocker;
import uta.fisei.cannongame.domain.entities.Cannon;
import uta.fisei.cannongame.domain.entities.Cannonball;
import uta.fisei.cannongame.domain.entities.Target;
import uta.fisei.cannongame.domain.entities.GameElement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
   // called when the app first launches
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }
}
