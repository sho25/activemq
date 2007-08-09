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
name|broker
operator|.
name|view
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
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|BrokerPlugin
import|;
end_import

begin_comment
comment|/**  * A<a href="http://www.graphviz.org/">DOT</a> file creator plugin which  * creates a DOT file showing the current connections  *   * @org.apache.xbean.XBean  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionDotFilePlugin
implements|implements
name|BrokerPlugin
block|{
specifier|private
name|String
name|file
init|=
literal|"ActiveMQConnections.dot"
decl_stmt|;
specifier|private
name|boolean
name|redrawOnRemove
decl_stmt|;
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|ConnectionDotFileInterceptor
argument_list|(
name|broker
argument_list|,
name|file
argument_list|,
name|redrawOnRemove
argument_list|)
return|;
block|}
specifier|public
name|String
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**      * Sets the destination file name to create the destination diagram      */
specifier|public
name|void
name|setFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
block|}
end_class

end_unit

