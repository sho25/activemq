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
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Referenceable
import|;
end_import

begin_comment
comment|/**  * Facilitates objects to be stored in JNDI as properties  */
end_comment

begin_interface
specifier|public
interface|interface
name|JNDIStorableInterface
extends|extends
name|Referenceable
block|{
comment|/**      * set the properties for this instance as retrieved from JNDI      *      * @param properties      */
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
function_decl|;
comment|/**      * Get the properties from this instance for storing in JNDI      *      * @return the properties that should be stored in JNDI      */
name|Properties
name|getProperties
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

