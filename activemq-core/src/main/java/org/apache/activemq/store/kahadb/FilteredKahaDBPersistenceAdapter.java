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
operator|.
name|kahadb
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMapEntry
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean element="filteredKahaDB"  *  */
end_comment

begin_class
specifier|public
class|class
name|FilteredKahaDBPersistenceAdapter
extends|extends
name|DestinationMapEntry
block|{
specifier|private
name|KahaDBPersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|private
name|boolean
name|perDestination
decl_stmt|;
specifier|public
name|FilteredKahaDBPersistenceAdapter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|FilteredKahaDBPersistenceAdapter
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|KahaDBPersistenceAdapter
name|adapter
parameter_list|)
block|{
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|=
name|adapter
expr_stmt|;
block|}
specifier|public
name|KahaDBPersistenceAdapter
name|getPersistenceAdapter
parameter_list|()
block|{
return|return
name|persistenceAdapter
return|;
block|}
specifier|public
name|void
name|setPersistenceAdapter
parameter_list|(
name|KahaDBPersistenceAdapter
name|persistenceAdapter
parameter_list|)
block|{
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
comment|// ok to have no destination, we default it
block|}
specifier|public
name|boolean
name|isPerDestination
parameter_list|()
block|{
return|return
name|perDestination
return|;
block|}
specifier|public
name|void
name|setPerDestination
parameter_list|(
name|boolean
name|perDestination
parameter_list|)
block|{
name|this
operator|.
name|perDestination
operator|=
name|perDestination
expr_stmt|;
block|}
block|}
end_class

end_unit

