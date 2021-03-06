// Copyright (c) 2018, Yubico AB
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.yubico.webauthn.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yubico.internal.util.EnumUtil;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;


/**
 * The attributes that are specified by a caller when referring to a credential as an input parameter to the create() or
 * get() methods. It mirrors the fields of the [[PublicKeyCredential]] object returned by the latter methods.
 */
@Value
@Builder
public class PublicKeyCredentialDescriptor implements Comparable<PublicKeyCredentialDescriptor> {

    /**
     * The type of the credential the caller is referring to.
     */
    @NonNull
    @Builder.Default
    private final PublicKeyCredentialType type = PublicKeyCredentialType.PUBLIC_KEY;

    /**
     * The identifier of the credential that the caller is referring to.
     */
    @NonNull
    private final ByteArray id;

    @NonNull
    @Builder.Default
    private final Optional<Set<AuthenticatorTransport>> transports = Optional.empty();

    public PublicKeyCredentialDescriptor(
        @NonNull PublicKeyCredentialType type,
        @NonNull ByteArray id,
        @NonNull Optional<Set<AuthenticatorTransport>> transports
    ) {
        this.type = type;
        this.id = id;
        this.transports = transports.map(TreeSet::new).map(Collections::unmodifiableSortedSet);
    }

    @JsonCreator
    private PublicKeyCredentialDescriptor(
        @NonNull @JsonProperty("type") PublicKeyCredentialType type,
        @NonNull @JsonProperty("id") ByteArray id,
        @JsonProperty("transports") Set<AuthenticatorTransport> transports
    ) {
        this(type, id, Optional.ofNullable(transports));
    }

    @Override
    public int compareTo(PublicKeyCredentialDescriptor other) {
        int idComparison = id.compareTo(other.id);
        if (idComparison != 0) {
            return idComparison;
        }

        if (type.compareTo(other.type) != 0) {
            return type.compareTo(other.type);
        }

        if (!transports.isPresent() && other.transports.isPresent()) {
            return -1;
        } else if (transports.isPresent() && !other.transports.isPresent()) {
            return 1;
        } else if (transports.isPresent() && other.transports.isPresent()){
            int transportsComparison = EnumUtil.compareSets(transports.get(), other.transports.get(), AuthenticatorTransport.class);
            if (transportsComparison != 0) {
                return transportsComparison;
            }
        }

        return 0;
    }

}
