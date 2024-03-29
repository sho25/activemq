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
name|util
operator|.
name|oxm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|XStream
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|HierarchicalStreamDriver
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|HierarchicalStreamReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|HierarchicalStreamWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|xml
operator|.
name|PrettyPrintWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|xml
operator|.
name|XppReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlpull
operator|.
name|mxp1
operator|.
name|MXParser
import|;
end_import

begin_comment
comment|/**  * Transforms object messages to text messages and vice versa using  * {@link XStream}  *   */
end_comment

begin_class
specifier|public
class|class
name|XStreamMessageTransformer
extends|extends
name|AbstractXMLMessageTransformer
block|{
specifier|private
name|XStream
name|xStream
decl_stmt|;
comment|/**      * Specialized driver to be used with stream readers and writers      */
specifier|private
name|HierarchicalStreamDriver
name|streamDriver
decl_stmt|;
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|XStream
name|getXStream
parameter_list|()
block|{
if|if
condition|(
name|xStream
operator|==
literal|null
condition|)
block|{
name|xStream
operator|=
name|createXStream
argument_list|()
expr_stmt|;
block|}
return|return
name|xStream
return|;
block|}
specifier|public
name|void
name|setXStream
parameter_list|(
name|XStream
name|xStream
parameter_list|)
block|{
name|this
operator|.
name|xStream
operator|=
name|xStream
expr_stmt|;
block|}
specifier|public
name|HierarchicalStreamDriver
name|getStreamDriver
parameter_list|()
block|{
return|return
name|streamDriver
return|;
block|}
specifier|public
name|void
name|setStreamDriver
parameter_list|(
name|HierarchicalStreamDriver
name|streamDriver
parameter_list|)
block|{
name|this
operator|.
name|streamDriver
operator|=
name|streamDriver
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|XStream
name|createXStream
parameter_list|()
block|{
return|return
operator|new
name|XStream
argument_list|()
return|;
block|}
comment|/**      * Marshalls the Object in the {@link ObjectMessage} to a string using XML      * encoding      */
specifier|protected
name|String
name|marshall
parameter_list|(
name|Session
name|session
parameter_list|,
name|ObjectMessage
name|objectMessage
parameter_list|)
throws|throws
name|JMSException
block|{
name|Serializable
name|object
init|=
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|StringWriter
name|buffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|HierarchicalStreamWriter
name|out
decl_stmt|;
if|if
condition|(
name|streamDriver
operator|!=
literal|null
condition|)
block|{
name|out
operator|=
name|streamDriver
operator|.
name|createWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|=
operator|new
name|PrettyPrintWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|getXStream
argument_list|()
operator|.
name|marshal
argument_list|(
name|object
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Unmarshalls the XML encoded message in the {@link TextMessage} to an      * Object      */
specifier|protected
name|Object
name|unmarshall
parameter_list|(
name|Session
name|session
parameter_list|,
name|TextMessage
name|textMessage
parameter_list|)
throws|throws
name|JMSException
block|{
name|HierarchicalStreamReader
name|in
decl_stmt|;
if|if
condition|(
name|streamDriver
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
name|streamDriver
operator|.
name|createReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|XppReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|,
operator|new
name|MXParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|getXStream
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

