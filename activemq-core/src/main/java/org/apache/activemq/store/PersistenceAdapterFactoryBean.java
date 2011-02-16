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
name|store
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|journal
operator|.
name|JournalPersistenceAdapterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|FactoryBean
import|;
end_import

begin_comment
comment|/**  * Creates a default persistence model using the Journal and JDBC  *   * @org.apache.xbean.XBean element="journaledJDBC"  *   *   */
end_comment

begin_class
specifier|public
class|class
name|PersistenceAdapterFactoryBean
extends|extends
name|JournalPersistenceAdapterFactory
implements|implements
name|FactoryBean
block|{
specifier|private
name|PersistenceAdapter
name|persistenceAdaptor
decl_stmt|;
specifier|public
name|Object
name|getObject
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|persistenceAdaptor
operator|==
literal|null
condition|)
block|{
name|persistenceAdaptor
operator|=
name|createPersistenceAdapter
argument_list|()
expr_stmt|;
block|}
return|return
name|persistenceAdaptor
return|;
block|}
specifier|public
name|Class
name|getObjectType
parameter_list|()
block|{
return|return
name|PersistenceAdapter
operator|.
name|class
return|;
block|}
specifier|public
name|boolean
name|isSingleton
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

