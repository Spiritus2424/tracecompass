/*******************************************************************************
 * Copyright (c) 2015, 2023 Ericsson
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.tracecompass.internal.ctf.core.event.metadata.tsdl.struct;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.tracecompass.ctf.core.event.metadata.DeclarationScope;
import org.eclipse.tracecompass.ctf.core.event.types.IDeclaration;
import org.eclipse.tracecompass.ctf.core.event.types.StructDeclaration;
import org.eclipse.tracecompass.ctf.core.trace.CTFTrace;
import org.eclipse.tracecompass.ctf.parser.CTFParser;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.AbstractScopedCommonTreeParser;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.CTFAntlrMetadataNode;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.JsonStructureFieldMemberMetadataNode;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.ParseException;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.tsdl.TypeAliasParser;
import org.eclipse.tracecompass.internal.ctf.core.event.metadata.tsdl.TypeDeclaratorParser;
import org.eclipse.tracecompass.internal.ctf.core.event.types.ICTFMetadataNode;

/**
 * Structures follow the ISO/C standard for structures
 *
 * @author Matthew Khouzam
 *
 */
public final class StructDeclarationParser extends AbstractScopedCommonTreeParser {

    /**
     * Parameter object
     *
     * @author Matthew Khouzam
     *
     */
    @NonNullByDefault
    public static final class Param implements ICommonTreeParserParameter {
        private final StructDeclaration fStruct;
        private final DeclarationScope fDeclarationScope;
        private final CTFTrace fTrace;

        /**
         * Constructor
         *
         * @param struct
         *            the structure declaration
         * @param trace
         *            the trace
         * @param scope
         *            the current scope
         */
        public Param(StructDeclaration struct, CTFTrace trace, DeclarationScope scope) {
            fStruct = struct;
            fTrace = trace;
            fDeclarationScope = scope;
        }
    }

    /**
     * The instance
     */
    public static final StructDeclarationParser INSTANCE = new StructDeclarationParser();

    private StructDeclarationParser() {
    }

    /**
     * Parses a declaration found in a struct.
     *
     * @param declaration
     *            A SV_DECLARATION node.
     * @param param
     *            The parameter object
     * @throws ParseException
     *             for poorly formed structs (duplicate fields, etc...)
     */
    @Override
    public StructDeclaration parse(ICTFMetadataNode declaration, ICommonTreeParserParameter param) throws ParseException {
        if (!(param instanceof Param)) {
            throw new IllegalArgumentException("Param must be a " + Param.class.getCanonicalName()); //$NON-NLS-1$
        }
        DeclarationScope scope = ((Param) param).fDeclarationScope;
        StructDeclaration struct = ((Param) param).fStruct;
        CTFTrace trace = ((Param) param).fTrace;
        StringBuilder identifierSB = new StringBuilder();
        IDeclaration decl = null;
        String fieldName = null;

        if (declaration instanceof CTFAntlrMetadataNode) {
            /* Get the type specifier list node */
            ICTFMetadataNode typeSpecifierListNode = declaration.getFirstChildWithType(CTFParser.tokenNames[CTFParser.TYPE_SPECIFIER_LIST]);

            if (typeSpecifierListNode == null) {
                throw new ParseException("Cannot have a struct without a type specifier"); //$NON-NLS-1$
            }

            /* Get the type declarator list node */
            ICTFMetadataNode typeDeclaratorListNode = declaration.getFirstChildWithType(CTFParser.tokenNames[CTFParser.TYPE_DECLARATOR_LIST]);

            if (typeDeclaratorListNode == null) {
                throw new ParseException("Cannot have a struct without a declarator"); //$NON-NLS-1$
            }

            /* Get the type declarator list */
            List<ICTFMetadataNode> typeDeclaratorList = typeDeclaratorListNode.getChildren();

            /*
             * For each type declarator, parse the declaration and add a field
             * to the struct
             */
            for (ICTFMetadataNode typeDeclaratorNode : typeDeclaratorList) {
                decl = TypeDeclaratorParser.INSTANCE.parse(typeDeclaratorNode, new TypeDeclaratorParser.Param(trace, typeSpecifierListNode, scope, identifierSB));
            }
            fieldName = identifierSB.toString();
        } else {
            decl = TypeAliasParser.INSTANCE.parse(declaration, new TypeAliasParser.Param(trace, scope));
            fieldName = ((JsonStructureFieldMemberMetadataNode) declaration).getName();
            if (fieldName == null) {
                fieldName = ((JsonStructureFieldMemberMetadataNode) declaration).getRole();
            }
        }

        scope.registerIdentifier(fieldName, decl);
        if (struct.hasField(fieldName)) {
            throw new ParseException("struct: duplicate field " //$NON-NLS-1$
                    + fieldName);
        }

        if (fieldName != null && decl != null) {
            struct.addField(fieldName, decl);
        }

        return struct;
    }

}
