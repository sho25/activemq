begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Faciliates objects to be stored in JNDI as properties  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JNDIBaseStorable
implements|implements
name|JNDIStorableInterface
block|{
specifier|private
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
comment|/**      * Set the properties that will represent the instance in JNDI      *      * @param props      */
specifier|protected
specifier|abstract
name|void
name|buildFromProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
function_decl|;
comment|/**      * Initialize the instance from properties stored in JNDI      *      * @param props      */
specifier|protected
specifier|abstract
name|void
name|populateProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
function_decl|;
comment|/**      * set the properties for this instance as retrieved from JNDI      *      * @param props      */
specifier|public
specifier|synchronized
name|void
name|setProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|props
expr_stmt|;
name|buildFromProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the properties from this instance for storing in JNDI      *      * @return the properties      */
specifier|public
specifier|synchronized
name|Properties
name|getProperties
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|properties
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
name|populateProperties
argument_list|(
name|this
operator|.
name|properties
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|properties
return|;
block|}
comment|/**      * Retrive a Reference for this instance to store in JNDI      *      * @return the built Reference      * @throws NamingException if error on building Reference      */
specifier|public
name|Reference
name|getReference
parameter_list|()
throws|throws
name|NamingException
block|{
return|return
name|JNDIReferenceFactory
operator|.
name|createReference
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

