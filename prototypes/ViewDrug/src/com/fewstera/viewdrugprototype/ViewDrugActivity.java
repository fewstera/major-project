package com.fewstera.viewdrugprototype;

import android.os.Bundle;
import com.fewstera.NHSPackage.*;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewDrugActivity extends Activity {
	
	private Drug _drug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_drug = loadParacetamol();
		
		setContentView(R.layout.activity_view_drug);

		loadDrugLayout();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_drug, menu);
		return true;
	}	
	
	private Drug loadParacetamol(){
		Drug paracetamol = new Drug();
		paracetamol.setName("Paracetamol");
		paracetamol.addDrugInformation(new DrugInformation("Medical uses", "Fever and Pain. Paracetamol is approved for reducing fever in people of all ages. Paracetamol is used for the relief of pains associated with many parts of the body. It has analgesic properties comparable to those of aspirin, while its anti-inflammatory effects are weaker. It is better tolerated than aspirin in patients in whom excessive gastric acid secretion or prolongation of bleeding time may be a concern."));
		paracetamol.addDrugInformation(new DrugInformation("Adverse effects", "Liver damage, Cancer, Skin reactions, Asthma and Overdose"));
		paracetamol.addDrugInformation(new DrugInformation("Structure and reactivity", "Paracetamol consists of a benzene ring core, substituted by one hydroxyl group and the nitrogen atom of an amide group in the para (1,4) pattern. The amide group is acetamide (ethanamide). It is an extensively conjugated system, as the lone pair on the hydroxyl oxygen, the benzene pi cloud, the nitrogen lone pair, the p orbital on the carbonyl carbon, and the lone pair on the carbonyl oxygen are all conjugated."));
		paracetamol.addDrugInformation(new DrugInformation("Lorem ipsum", " Phasellus condimentum dignissim vulputate. Duis in pulvinar felis, ut vehicula tellus. Pellentesque hendrerit vehicula tellus nec egestas. Curabitur consectetur risus in malesuada tristique. Pellentesque posuere ante ac enim elementum, a viverra arcu mattis. Donec porta augue pretium, porta orci sit amet, egestas metus. Maecenas non consectetur nisl, egestas feugiat risus. Sed dolor dui, tempus sit amet nunc vel, mattis tincidunt est. Nunc ultricies mi sit amet lacus mollis suscipit. Duis tempus interdum convallis. Donec volutpat justo et quam consectetur, ac blandit tortor elementum. Curabitur eu libero magna. Sed lobortis iaculis enim, et fermentum mi scelerisque et. Maecenas eu lorem ac turpis euismod accumsan. Donec eget justo at odio fermentum dictum vel vitae massa. Morbi malesuada dolor ut neque consectetur vestibulum."));
		paracetamol.addDrugInformation(new DrugInformation("Nam vitae ipsum", "Pellentesque eu odio molestie, lacinia tellus eu, aliquet nisi. Etiam aliquet luctus leo, eget commodo arcu lobortis at. Curabitur egestas, leo eget elementum semper, risus nulla tempor diam, eu eleifend turpis mi at tortor. Cras varius massa purus, at adipiscing magna tincidunt vitae. Cras sed porttitor ante. Phasellus hendrerit turpis vel metus lacinia interdum. Duis nec elit libero. Donec sagittis tempor urna, vel egestas magna accumsan id. Nullam at sollicitudin turpis, vel eleifend dolor. Proin nec iaculis ante, et molestie lectus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus ornare rutrum massa, sed fringilla neque vestibulum in."));
		paracetamol.addDrugInformation(new DrugInformation("Donec dictum", "Nunc convallis, nibh quis varius consectetur, dui ligula pretium purus, lobortis vehicula orci dolor ut nibh. Praesent tempor venenatis facilisis. Proin massa metus, gravida vitae consectetur nec, scelerisque sit amet diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Integer ultricies porttitor odio vel volutpat. In hac habitasse platea dictumst. Donec dictum porttitor pulvinar."));
		return paracetamol;
	}
	
	private void loadDrugLayout(){
		String drugName = _drug.getName();
		
		setTitle(drugName);
		
		TextView drugHeader = (TextView) findViewById(R.id.drug_name_header);
		drugHeader.setText(drugName);
		
		LayoutInflater inflater = getLayoutInflater();
		for(DrugInformation information: _drug.getDrugInformations()){
			View newInformation = inflater.inflate(R.layout.drug_information, null, false);
			
			TextView infoName = (TextView) newInformation.findViewById(R.id.information_name);
			infoName.setText(information.getName());
			
			TextView infoContent = (TextView) newInformation.findViewById(R.id.information_content);
			infoContent.setText(information.getInformation());	
			
			((ViewGroup) findViewById(R.id.drug_informations)).addView(newInformation);
		}
	}



}
