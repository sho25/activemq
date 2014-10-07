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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionMetaData
import|;
end_import

begin_comment
comment|/**  * A<CODE>ConnectionMetaData</CODE> object provides information describing  * the<CODE>Connection</CODE> object.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ActiveMQConnectionMetaData
implements|implements
name|ConnectionMetaData
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PROVIDER_VERSION
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PROVIDER_MAJOR_VERSION
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PROVIDER_MINOR_VERSION
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQConnectionMetaData
name|INSTANCE
init|=
operator|new
name|ActiveMQConnectionMetaData
argument_list|()
decl_stmt|;
static|static
block|{
name|String
name|version
init|=
literal|null
decl_stmt|;
name|int
name|major
init|=
literal|0
decl_stmt|;
name|int
name|minor
init|=
literal|0
decl_stmt|;
try|try
block|{
name|Package
name|p
init|=
name|Package
operator|.
name|getPackage
argument_list|(
literal|"org.apache.activemq"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|version
operator|=
name|p
operator|.
name|getImplementationVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+)\\.(\\d+).*"
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|major
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|minor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
name|PROVIDER_VERSION
operator|=
name|version
expr_stmt|;
name|PROVIDER_MAJOR_VERSION
operator|=
name|major
expr_stmt|;
name|PROVIDER_MINOR_VERSION
operator|=
name|minor
expr_stmt|;
block|}
specifier|private
name|ActiveMQConnectionMetaData
parameter_list|()
block|{     }
comment|/**      * Gets the JMS API version.      *      * @return the JMS API version      */
annotation|@
name|Override
specifier|public
name|String
name|getJMSVersion
parameter_list|()
block|{
return|return
literal|"1.1"
return|;
block|}
comment|/**      * Gets the JMS major version number.      *      * @return the JMS API major version number      */
annotation|@
name|Override
specifier|public
name|int
name|getJMSMajorVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**      * Gets the JMS minor version number.      *      * @return the JMS API minor version number      */
annotation|@
name|Override
specifier|public
name|int
name|getJMSMinorVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**      * Gets the JMS provider name.      *      * @return the JMS provider name      */
annotation|@
name|Override
specifier|public
name|String
name|getJMSProviderName
parameter_list|()
block|{
return|return
literal|"ActiveMQ"
return|;
block|}
comment|/**      * Gets the JMS provider version.      *      * @return the JMS provider version      */
annotation|@
name|Override
specifier|public
name|String
name|getProviderVersion
parameter_list|()
block|{
return|return
name|PROVIDER_VERSION
return|;
block|}
comment|/**      * Gets the JMS provider major version number.      *      * @return the JMS provider major version number      */
annotation|@
name|Override
specifier|public
name|int
name|getProviderMajorVersion
parameter_list|()
block|{
return|return
name|PROVIDER_MAJOR_VERSION
return|;
block|}
comment|/**      * Gets the JMS provider minor version number.      *      * @return the JMS provider minor version number      */
annotation|@
name|Override
specifier|public
name|int
name|getProviderMinorVersion
parameter_list|()
block|{
return|return
name|PROVIDER_MINOR_VERSION
return|;
block|}
comment|/**      * Gets an enumeration of the JMSX property names.      *      * @return an Enumeration of JMSX property names      */
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getJMSXPropertyNames
parameter_list|()
block|{
name|Vector
argument_list|<
name|String
argument_list|>
name|jmxProperties
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|jmxProperties
operator|.
name|add
argument_list|(
literal|"JMSXUserID"
argument_list|)
expr_stmt|;
name|jmxProperties
operator|.
name|add
argument_list|(
literal|"JMSXGroupID"
argument_list|)
expr_stmt|;
name|jmxProperties
operator|.
name|add
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
expr_stmt|;
name|jmxProperties
operator|.
name|add
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
expr_stmt|;
name|jmxProperties
operator|.
name|add
argument_list|(
literal|"JMSXProducerTXID"
argument_list|)
expr_stmt|;
return|return
name|jmxProperties
operator|.
name|elements
argument_list|()
return|;
block|}
block|}
end_class

end_unit

