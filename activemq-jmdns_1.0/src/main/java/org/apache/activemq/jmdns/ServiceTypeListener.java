begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright 2003-2005 Arthur van Hoff, Rick Blair
end_comment

begin_comment
comment|//Licensed under Apache License version 2.0
end_comment

begin_comment
comment|//Original license LGPL
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_comment
comment|/**  * Listener for service types.  *  * @version %I%, %G%  * @author	Arthur van Hoff, Werner Randelshofer  */
end_comment

begin_interface
specifier|public
interface|interface
name|ServiceTypeListener
extends|extends
name|EventListener
block|{
comment|/**      * A new service type was discovered.      *      * @param event The service event providing the fully qualified type of      *              the service.      */
name|void
name|serviceTypeAdded
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

