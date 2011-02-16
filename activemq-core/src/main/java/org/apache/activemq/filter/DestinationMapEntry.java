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
name|filter
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|PostConstruct
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
name|command
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A base class for entry objects used to construct a destination based policy  * map.  *   *   * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DestinationMapEntry
implements|implements
name|Comparable
block|{
specifier|private
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|instanceof
name|DestinationMapEntry
condition|)
block|{
name|DestinationMapEntry
name|thatEntry
init|=
operator|(
name|DestinationMapEntry
operator|)
name|that
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|compare
argument_list|(
name|destination
argument_list|,
name|thatEntry
operator|.
name|destination
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * A helper method to set the destination from a configuration file      */
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * A helper method to set the destination from a configuration file      */
specifier|public
name|void
name|setTopic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTempTopic
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|setDestination
argument_list|(
operator|new
name|ActiveMQTempTopic
argument_list|(
literal|">"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTempQueue
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|setDestination
argument_list|(
operator|new
name|ActiveMQTempQueue
argument_list|(
literal|">"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      *      * @throws Exception      * @org.apache.xbean.InitMethod      */
annotation|@
name|PostConstruct
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You must specify the 'destination' property"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

