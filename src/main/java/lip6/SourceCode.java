package lip6;

import org.treesitter.TSNode;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SourceCode {
    private static String code;
    public static void setCode(String code) {
        SourceCode.code = code;
    }
    public static String getCode() {
        return code;
    }
    public static String getText(TSNode node){
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        return new String(code.getBytes(StandardCharsets.UTF_8), startByte, endByte - startByte, StandardCharsets.UTF_8);
    }
    private static String getIdentifierChild(TSNode node){
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("identifier")) {
                return getText(child);
            }
        }
        return null;

    }

    private static String getVariableDeclaratorChild(TSNode node){
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("variable_declarator")) {
                return getIdentifierChild(child);
            }
        }
        return null;

    }
    private static String getTypeIdentifier(TSNode node){
        List<String> javaTypes = List.of("integral_type", "floating_point_type", "boolean_type","array_type");
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("type_identifier") || javaTypes.contains(child.getType())) {
                return getText(child);
            }
        }
        return null;

    }
    private static String getFormalParameter(TSNode node){
        StringBuilder formalParameters = new StringBuilder();
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("formal_parameter")) {
                formalParameters.append(getTypeIdentifier(child));
            }else {
                formalParameters.append(getText(child));// add parentheses and commas
            }
        }
        return formalParameters.toString();

    }
    private static String getFormalParameters(TSNode node){
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("formal_parameters")) {
                return getFormalParameter(child);
            }
        }
        return null;

    }
    private static String getScopedIdentifier(TSNode node){
        if (getIdentifierChild(node) != null) {
            return getIdentifierChild(node);
        }
        StringBuilder scopedIdentifier = new StringBuilder();
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("scoped_identifier")) {
                scopedIdentifier.append(getText(child));
            }
        }
        return scopedIdentifier.toString();
    }
    private static boolean hasAsterisk(TSNode node){
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            if (child.getType().equals("asterisk")) {
                return true;
            }
        }
        return false;
    }

    public static String getName(TSNode node){
        if(node.toString() == null){
            return null;
        }
        String nodeType = node.getType();
        switch (nodeType) {
            case "package_declaration" -> {
                return getScopedIdentifier(node);
            }
            case "import_declaration" -> {
                StringBuilder importDeclarationName = new StringBuilder();
                importDeclarationName.append(getScopedIdentifier(node));
                if (hasAsterisk(node)) {
                    importDeclarationName.append(".*");
                }
                return importDeclarationName.toString();
            }
            case "class_declaration" -> {
                return getIdentifierChild(node);
            }
            case "method_declaration", "constructor_declaration" -> {
                return getIdentifierChild(node) + getFormalParameters(node);
            }
            case "field_declaration", "local_variable_declaration" -> {
                return getVariableDeclaratorChild(node);
            }
            case "type_identifier" -> {
                return getText(node);
            }
            default -> {
                return node.getType();
            }

        }
//        String identifier = getIdentifierChild(node);
//        if(identifier != null){
//            return identifier;
//        }
//        return node.getType();
    }
}