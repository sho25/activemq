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
name|tool
operator|.
name|properties
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

begin_class
specifier|public
class|class
name|JmsFactoryProperties
extends|extends
name|AbstractObjectProperties
block|{
name|Properties
name|factorySettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|acceptConfig
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
comment|// Since we do not know the specific properties of each factory,
comment|// lets cache it first and give it to the spi later
name|factorySettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|public
name|Properties
name|getFactorySettings
parameter_list|()
block|{
return|return
name|factorySettings
return|;
block|}
block|}
end_class

end_unit

