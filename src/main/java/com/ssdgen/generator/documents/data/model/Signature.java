package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Signature {

    private String fullPath;
    private String name;
    private String label;

    public Signature(String fullPath, String name) {
        this.fullPath = fullPath;
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return "Signature{" +
                "fullPath='" + fullPath + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    public Signature() {
    }

    public static class Generator implements ModelGenerator<Signature> {

        private static final Map<String, String> signatureLabels = new LinkedHashMap<>();

        {
              signatureLabels.put("Signature du service des finances", "fr");
              signatureLabels.put("Signataire autorisé", "fr");
              signatureLabels.put("Représentation légale", "fr");
              signatureLabels.put("Responsable des finances", "fr");
              signatureLabels.put("Gestionnaire de crédit", "fr");

              signatureLabels.put("Finance Dep. Signature", "en");
              signatureLabels.put("Authorized Signatory", "en");
              signatureLabels.put("Legal Representation", "en");
              signatureLabels.put("Finance Manager", "en");
              signatureLabels.put("Credit Manager", "en");
              signatureLabels.put("Approved by", "en");
        }

        private static final List<String> signaturesFileList = Arrays.asList(
            "common/signature/kaggle_real_forged/metadata.json",
            "common/signature/multi_script_handwritten_signature/metadata.json");
        private static final List<Signature> signatures = new ArrayList<Signature>();
        {
            for (String signatureFile : signaturesFileList) {
                Reader jsonReader = new InputStreamReader(Signature.class.getClassLoader().getResourceAsStream(signatureFile));
                Gson gson = new Gson();
                Type collectionType = new TypeToken<List<Signature>>(){}.getType();
                signatures.addAll(gson.fromJson(jsonReader, collectionType));
            }
        }

        @Override
        public Signature generate(GenerationContext ctx) {
            Signature signatureObj = signatures.get(ctx.getRandom().nextInt(signatures.size()));
            List<String> sigLabels = signatureLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxS = ctx.getRandom().nextInt(sigLabels.size());
            signatureObj.setLabel(sigLabels.get(idxS));
            return signatureObj;
        }
    }

}
