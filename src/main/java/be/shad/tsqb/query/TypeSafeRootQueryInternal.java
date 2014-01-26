package be.shad.tsqb.query;

import be.shad.tsqb.selection.TypeSafeQueryProjections;

public interface TypeSafeRootQueryInternal extends TypeSafeRootQuery, TypeSafeQueryInternal {

	TypeSafeQueryProjections getProjections();
	
}
