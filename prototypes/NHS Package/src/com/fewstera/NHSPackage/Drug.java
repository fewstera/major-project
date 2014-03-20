/**
 * 
 */
package com.fewstera.NHSPackage;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Drug implements Parcelable{
	
	private int _id;
	private String _name;
	private ArrayList<DrugInformation> _drugInformations = new ArrayList<DrugInformation>();

	public Drug(){
		super();
	}
	
	//Returns the drugs id
	public int getId(){
		return _id;
	}
	
	//Returns the drugs name
	public String getName(){
		return _name;
	}
	
	//Get an ArrayList of all the drug information
	public ArrayList<DrugInformation> getDrugInformations(){
		return _drugInformations;
	}
	

	public void setId(int id){
		_id = id;
	}
	
	public void setName(String name){
		_name = name;
	}
	
	//Add new information about a drug
	public void addDrugInformation(DrugInformation drugInfo){
		_drugInformations.add(drugInfo);
	}
	
	
	/* 
	 * BELOW IS THE METHODS FOR MAKING THE OBJECT PARCELABLE 
	 * This is used so that drugs can be pa
	 */
	
	public Drug(Parcel in) {
		_id = in.readInt();
		_name = in.readString();
		in.readList(_drugInformations, null);
	}

	public static final Parcelable.Creator<Drug> CREATOR = new Parcelable.Creator<Drug>() {
		public Drug createFromParcel(Parcel in) {
			return new Drug(in);
		}

		public Drug[] newArray(int size) {
			return new Drug[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(_name);
		dest.writeList(_drugInformations);
	}
	
}
