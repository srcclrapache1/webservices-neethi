/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.security.policy.builders;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.neethi.builders.AssertionBuilder;
import org.apache.ws.security.policy.Constants;
import org.apache.ws.security.policy.model.AlgorithmSuite;
import org.apache.ws.security.policy.model.SignedEncryptedElements;
import org.apache.ws.security.policy.model.SignedEncryptedParts;
import org.apache.ws.security.policy.model.SupportingToken;
import org.apache.ws.security.policy.model.Token;

public class SupportingTokensBuilder implements AssertionBuilder {

    public Assertion build(OMElement element, AssertionBuilderFactory factory) throws IllegalArgumentException {
        QName name = element.getQName();
        SupportingToken supportingToken = null;
        
        if (Constants.SUPPORIING_TOKENS.equals(name)) {
            supportingToken = new SupportingToken(Constants.SUPPORTING_TOKEN_SUPPORTING);
        } else if (Constants.SIGNED_SUPPORTING_TOKENS.equals(name)) {
            supportingToken = new SupportingToken(Constants.SUPPORTING_TOKEN_SIGNED);
        } else if (Constants.ENDORSING_SUPPORTING_TOKENS.equals(name)) {
            supportingToken = new SupportingToken(Constants.SUPPORTING_TOKEN_ENDORSING);
        } else if (Constants.SIGNED_ENDORSING_SUPPORTING_TOKENS.equals(name)) {
            supportingToken = new SupportingToken(Constants.SUPPORTING_TOKEN_SIGNED_ENDORSING);
        }
                
        Policy policy = (Policy) PolicyEngine.getPolicy(element);
        policy = (Policy) policy.normalize(false);
        
        for (Iterator iterator = policy.getAlternatives(); iterator.hasNext();) {
            processAlternative((List) iterator.next(), supportingToken);
        }
        
        return supportingToken;
    }
    
    private void processAlternative(List assertions, SupportingToken parent) {
        SupportingToken supportingToken = new SupportingToken(parent.getTokenType());
        
        for (Iterator iterator = assertions.iterator(); iterator.hasNext();) {
            
            Assertion primitive = (Assertion) iterator.next();
            QName qname = primitive.getName();
            
            if (Constants.ALGORITHM_SUITE.equals(qname)) {
                supportingToken.setAlgorithmSuite((AlgorithmSuite) primitive);
                
            } else if (Constants.SIGNED_PARTS.equals(qname)) {
                supportingToken.setSignedParts((SignedEncryptedParts) primitive);
                
            } else if (Constants.SIGNED_ELEMENTS.equals(qname)) {
                supportingToken.setSignedElements((SignedEncryptedElements) primitive);
                
            } else if (Constants.ENCRYPTED_PARTS.equals(qname)) {
                supportingToken.setEncryptedParts((SignedEncryptedParts) primitive);
                
            } else if (Constants.ENCRYPTED_ELEMENTS.equals(qname)) {
                supportingToken.setEncryptedElements((SignedEncryptedElements) primitive);
                
            } else if (primitive instanceof Token) {
                supportingToken.addToken((Token) primitive);
            }
        }
        
        parent.addOption(supportingToken);        
    }
    

}