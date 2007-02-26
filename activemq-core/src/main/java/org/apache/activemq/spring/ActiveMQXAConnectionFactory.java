begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|spring
package|;
end_package

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
name|BeanNameAware
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
name|InitializingBean
import|;
end_import

begin_comment
comment|/**  * A<a href="http://www.springframework.org/">Spring</a> enhanced XA connection  * factory which will automatically use the Spring bean name as the clientIDPrefix property  * so that connections created have client IDs related to your Spring.xml file for  * easier comprehension from<a href="http://activemq.apache.org/jmx.html">JMX</a>.  *   * @org.apache.xbean.XBean element="xaConnectionFactory"  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQXAConnectionFactory
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQXAConnectionFactory
implements|implements
name|InitializingBean
implements|,
name|BeanNameAware
block|{
specifier|private
name|String
name|beanName
decl_stmt|;
specifier|private
name|boolean
name|useBeanNameAsClientIdPrefix
decl_stmt|;
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|isUseBeanNameAsClientIdPrefix
argument_list|()
operator|&&
name|getClientIDPrefix
argument_list|()
operator|==
literal|null
condition|)
block|{
name|setClientIDPrefix
argument_list|(
name|getBeanName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getBeanName
parameter_list|()
block|{
return|return
name|beanName
return|;
block|}
specifier|public
name|void
name|setBeanName
parameter_list|(
name|String
name|beanName
parameter_list|)
block|{
name|this
operator|.
name|beanName
operator|=
name|beanName
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseBeanNameAsClientIdPrefix
parameter_list|()
block|{
return|return
name|useBeanNameAsClientIdPrefix
return|;
block|}
specifier|public
name|void
name|setUseBeanNameAsClientIdPrefix
parameter_list|(
name|boolean
name|useBeanNameAsClientIdPrefix
parameter_list|)
block|{
name|this
operator|.
name|useBeanNameAsClientIdPrefix
operator|=
name|useBeanNameAsClientIdPrefix
expr_stmt|;
block|}
block|}
end_class

end_unit

