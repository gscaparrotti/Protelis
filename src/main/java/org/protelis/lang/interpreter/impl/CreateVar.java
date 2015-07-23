/*
 * Copyright (C) 2010-2015, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.protelis.lang.interpreter.impl;

import org.danilopianini.lang.util.FasterString;
import org.protelis.lang.interpreter.AnnotatedTree;
import org.protelis.vm.ExecutionContext;

/**
 * @author Danilo Pianini
 *
 */
public class CreateVar extends AbstractAnnotatedTree<Object> {

	private static final long serialVersionUID = -7298208661255971616L;
	private final FasterString var;
	private final boolean definition;
	
	/**
	 * @param name
	 *            variable name
	 * @param value
	 *            program to evaluate to compute the value
	 * @param isDefinition
	 *            true if it is a let
	 */
	public CreateVar(final String name, final AnnotatedTree<?> value, final boolean isDefinition) {
		this(new FasterString(name), value, isDefinition);
	}

	/**
	 * @param name
	 *            variable name
	 * @param value
	 *            program to evaluate to compute the value
	 * @param isDefinition
	 *            true if it is a let
	 */
	public CreateVar(final FasterString name, final AnnotatedTree<?> value, final boolean isDefinition) {
		super(value);
		var = name;
		definition = isDefinition;
	}

	@Override
	public AnnotatedTree<Object> copy() {
		return new CreateVar(var, deepCopyBranches().get(0), definition);
	}

	@Override
	public void eval(final ExecutionContext context) {
		projectAndEval(context);
		final Object res = getBranch(0).getAnnotation();
		context.putVariable(var, res, isDefinition());
		setAnnotation(res);
	}

	@Override
	protected void asString(final StringBuilder sb, final int i) {
		sb.append(var);
		sb.append(" = \n");
		getBranch(0).toString(sb, i + 1);
	}
	
	/**
	 * @return true if it is a let
	 */
	public boolean isDefinition() {
		return definition;
	}
	
	/**
	 * @return the variable name
	 */
	public FasterString getVarName() {
		return var;
	}

}