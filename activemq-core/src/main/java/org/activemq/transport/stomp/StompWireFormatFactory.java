begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormatFactory
import|;
end_import

begin_comment
comment|/**  * Creates WireFormat objects that implement the<a href="http://stomp.codehaus.org/">Stomp</a> protocol.  */
end_comment

begin_class
specifier|public
class|class
name|StompWireFormatFactory
implements|implements
name|WireFormatFactory
block|{
specifier|public
name|WireFormat
name|createWireFormat
parameter_list|()
block|{
return|return
operator|new
name|StompWireFormat
argument_list|()
return|;
block|}
block|}
end_class

end_unit

