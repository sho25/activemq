begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|tcp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
specifier|public
class|class
name|StubX509Certificate
extends|extends
name|X509Certificate
block|{
specifier|private
specifier|final
name|Principal
name|id
decl_stmt|;
specifier|public
name|StubX509Certificate
parameter_list|(
name|Principal
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|Principal
name|getSubjectDN
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|// --- Stubbed Methods ---
specifier|public
name|void
name|checkValidity
parameter_list|()
block|{     }
specifier|public
name|void
name|checkValidity
parameter_list|(
name|Date
name|arg0
parameter_list|)
block|{     }
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|BigInteger
name|getSerialNumber
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Principal
name|getIssuerDN
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Date
name|getNotBefore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Date
name|getNotAfter
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|getTBSCertificate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getSigAlgName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getSigAlgOID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|getSigAlgParams
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
index|[]
name|getIssuerUniqueID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
index|[]
name|getSubjectUniqueID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
index|[]
name|getKeyUsage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getBasicConstraints
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|byte
index|[]
name|getEncoded
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|verify
parameter_list|(
name|PublicKey
name|arg0
parameter_list|)
block|{     }
specifier|public
name|void
name|verify
parameter_list|(
name|PublicKey
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{     }
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|PublicKey
name|getPublicKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|hasUnsupportedCriticalExtension
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|public
name|Set
name|getCriticalExtensionOIDs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|public
name|Set
name|getNonCriticalExtensionOIDs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|getExtensionValue
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
