begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*  * Copyright (c) 2007, 2008 XStream Committers.  * All rights reserved.  *  * The software in this package is published under the terms of the BSD  * style license a copy of which has been included with this distribution in  * the LICENSE.txt file.  *   * Created on 30. March 2007 by Joerg Schaible  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|mapped
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|mapped
operator|.
name|MappedNamespaceConvention
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|mapped
operator|.
name|MappedXMLInputFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|mapped
operator|.
name|MappedXMLOutputFactory
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
name|StreamException
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
name|json
operator|.
name|JettisonStaxWriter
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
name|QNameMap
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
name|StaxReader
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
name|StaxWriter
import|;
end_import

begin_comment
comment|/**  *   * Temporary used until XStream 1.3.2 is released  *   * Simple XStream driver wrapping Jettison's Mapped reader and writer. Serializes object from  * and to JSON.  *   * @author Dejan Bosanac  */
end_comment

begin_class
specifier|public
class|class
name|JettisonMappedXmlDriver
implements|implements
name|HierarchicalStreamDriver
block|{
specifier|private
specifier|final
name|MappedXMLOutputFactory
name|mof
decl_stmt|;
specifier|private
specifier|final
name|MappedXMLInputFactory
name|mif
decl_stmt|;
specifier|private
specifier|final
name|MappedNamespaceConvention
name|convention
decl_stmt|;
specifier|private
name|boolean
name|useSerializeAsArray
init|=
literal|true
decl_stmt|;
specifier|public
name|JettisonMappedXmlDriver
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JettisonMappedXmlDriver
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|boolean
name|useSerializeAsArray
parameter_list|)
block|{
name|mof
operator|=
operator|new
name|MappedXMLOutputFactory
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|mif
operator|=
operator|new
name|MappedXMLInputFactory
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|convention
operator|=
operator|new
name|MappedNamespaceConvention
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|useSerializeAsArray
operator|=
name|useSerializeAsArray
expr_stmt|;
block|}
specifier|public
name|HierarchicalStreamReader
name|createReader
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|StaxReader
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mif
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StreamException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|HierarchicalStreamReader
name|createReader
parameter_list|(
specifier|final
name|InputStream
name|input
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|StaxReader
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mif
operator|.
name|createXMLStreamReader
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StreamException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|HierarchicalStreamWriter
name|createWriter
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|useSerializeAsArray
condition|)
block|{
return|return
operator|new
name|JettisonStaxWriter
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mof
operator|.
name|createXMLStreamWriter
argument_list|(
name|writer
argument_list|)
argument_list|,
name|convention
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|StaxWriter
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mof
operator|.
name|createXMLStreamWriter
argument_list|(
name|writer
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StreamException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|HierarchicalStreamWriter
name|createWriter
parameter_list|(
specifier|final
name|OutputStream
name|output
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|useSerializeAsArray
condition|)
block|{
return|return
operator|new
name|JettisonStaxWriter
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mof
operator|.
name|createXMLStreamWriter
argument_list|(
name|output
argument_list|)
argument_list|,
name|convention
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|StaxWriter
argument_list|(
operator|new
name|QNameMap
argument_list|()
argument_list|,
name|mof
operator|.
name|createXMLStreamWriter
argument_list|(
name|output
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StreamException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

